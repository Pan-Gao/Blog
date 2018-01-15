## Lucene
### 基本原理
Lucene是非常优秀的成熟的开源的免费的纯java语言的全文索引检索工具包。
全文检索 是指计算机索引程序通过扫描文章中的每一个词，对每 一个词建立一个索引，指明该词在文章中出现的次数和位置，当用 户查询时，检索程序就根据事先建立的索引进行查找，并将查找的 结果反馈给用户的检索方式。
Lucene是一个高性能、可伸缩的信息搜索(IR)库。 Information Retrieval (IR) library.它使你可以为你的应用程序添加索引和搜索能力。

#### 全文检索系统的结构：
![](https://github.com/yuanrw/Blog/blob/master/note/pic/lucene1.png)

#### lucene的优点主要有：
索引文件格式独立于应用平台。Lucene定义了一套以8位字节为基础的索引文件格式，使得兼容系统或者不同平台的应用能够共享建立的索引文件。
在传统全文检索引擎的倒排索引的基础上，实现了分块索引，能够针对新的文件建立小文件索引，升索引速度。然后通过与原有索引的合并，达到优化的目的。
优秀的面向对象的系统架构，使得对于Lucene扩展的学习难度降低，方便扩充新功能。

#### 倒排索引的原理
![](https://github.com/yuanrw/Blog/blob/master/note/pic/lucene2.png)

### 使用方法
lucene每个版本使用方法有一些不同。
我用的版本是6.6.0
参见：[官方文档](http://lucene.apache.org/core/6_6_0/core/index.html)

引入依赖：
```
<!-- lucene -->
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-core</artifactId><!--lucene核心-->
    <version>6.6.0</version>
</dependency>
    <dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-analyzers-common</artifactId><!--分词器-->
    <version>6.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-analyzers-smartcn</artifactId><!--中文分词器-->
    <version>6.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queryparser</artifactId><!--格式化-->
    <version>6.6.0</version>
</dependency>
```
代码在com.yrw.util.LuceneUtils包下。

这边要注意一个问题，通常情况下更新索引（更新，创建博文）要新建一个IndexWriter，执行完相应操作后再关闭IndexWriter：
```
IndexWriter.close();
```

如果观察一下索引文档，会发现有个**write.lock**文件。**IndexWriter对象在实例化时获得write.lock文件，直到IndexWriter对象关闭之后才释放。当IndexReader对象在删除、恢复删除文档或设定域规范时，也需要获得这个文件。**因此，write.lock会在对索引进行写操作时锁定索引。

当对段进行读或合并操作时，就需要用到commit.lock文件。
在IndexReader对象读取段文件之前会获取commit.lock文件，在这个锁文件中对所有的索引段进行了命名，只有当IndexReader对象已经打开并读取完所有的段后，Lucene才会释放这个锁文件。IndexWriter对象在创建新的段之前，也需要获得commit.lock文件，并一直对其进行维护，直至该对象执行诸如段合并等操作，并将无用的索引文件移除完毕之后才释放。

总结：
**每次只能有一个IndexWriter或者IndexReader对索引进行修改**，但是查找不要紧。

解决方法：
由于**IndexWriter和IndexReader这两个类本身是线程安全的**，也就是说，这两个类的实例都可以被多线程共享，Lucene会对各个线程中所有对索引进行修改的方法的调用进行恰当的同步处理，以此来确保修改操作能一个接着一个地有序进行。

可以把这两个类做成单例。整个应用都用同一个IndexWriter和IndexReader。
单例模式有多种实现方法，可以用枚举实现。

**注意**在初始化IndexWriter时，IndexWriterConfig.OpenMode有三种模式：
```
/**
* Specifies the open mode for {@link IndexWriter}.
*/
public static enum OpenMode {
    /** 
    * Creates a new index or overwrites an existing one. 
    */
    CREATE,

    /** 
    * Opens an existing index. 
    */
    APPEND,

    /** 
    * Creates a new index if one does not exist,
    * otherwise it opens the index and documents will be appended. 
    */
    CREATE_OR_APPEND 
}
```
默认模式是CREATE_OR_APPEND。

**在修改过索引之后，IndexReader也要更新**，否则会查不到新的内容。
```
public IndexReader getIndexReader() throws IOException {

    if(DirectoryReader.openIfChanged((DirectoryReader)indexReader)==null) {
        return indexReader;
    }else {
        return indexReader = DirectoryReader.openIfChanged((DirectoryReader)indexReader);
    }
}
```
**openIfChanged()方法的作用：If the index has changed since the provided reader was opened, open and return a new reader; else, return null.**

参考资料：
[博客：Lucene的并发性安全性以及锁](https://www.cnblogs.com/likehua/archive/2012/02/16/2354532.html)
[单例模式的几种实现方法](http://www.importnew.com/18872.html)
[Lucene 6.6.0 官方文档](http://lucene.apache.org/core/6_6_0/core/index.html)
