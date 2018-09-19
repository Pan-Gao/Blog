package com.yrw.util;

import com.yrw.model.Blog;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class LuceneUtils {

    private static final Logger logger = LoggerFactory.getLogger(LuceneUtils.class);
    private static final String DEFAULT_LUCENE_DIR = System.getProperty("user.dir") + "/lucene";

    //创建
    public static void createIndex(Blog blog) throws IOException {
        IndexWriter writer = Writer.INSTANCE.getIndexWriter();
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES)); //对标题做索引
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));//对文章内容做索引
        writer.addDocument(doc);
        writer.commit();
    }

    //更新索引
    public static void updateIndex(Blog blog) throws IOException {
        IndexWriter writer = Writer.INSTANCE.getIndexWriter();
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));
        writer.updateDocument(new Term("id", String.valueOf(blog.getId())), doc); //更新索引
        writer.commit();
    }

    //搜索
    public static List<Blog> search(String keyword, int pageStart, int pageSize) throws IOException, ParseException {
        IndexReader reader = Reader.INSTANCE.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser1 = new QueryParser("title", analyzer);//对文章标题进行搜索
        Query query1 = parser1.parse(keyword);
        QueryParser parser2 = new QueryParser("content", analyzer);//对文章内容进行搜索
        Query query2 = parser2.parse(keyword);
        booleanQuery.add(query1, Occur.SHOULD);
        booleanQuery.add(query2, Occur.SHOULD);
        ScoreDoc lastBottom = getLastScoreDoc(pageStart, pageSize, booleanQuery.build(), searcher);
        TopDocs hits = searcher.searchAfter(lastBottom, booleanQuery.build(), pageSize);
        List<Blog> blogs = new LinkedList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Blog blog = new Blog();
            blog.setId(Long.parseLong(doc.get(("id"))));
            blog.setTitle(doc.get("title"));
            blog.setContent(doc.get("content"));
            blogs.add(blog);
        }
        return blogs;
    }

    //用于分页
    private static ScoreDoc getLastScoreDoc(int pageStart, int pageSize, Query query, IndexSearcher searcher) throws IOException {
        if (pageStart == 1) return null;//如果是第一页就返回空
        int num = pageSize * (pageStart - 1);//获取上一页的最后数量
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }

    //删除
    public static void deleteIndex(Long id) throws IOException {
        IndexWriter writer = Writer.INSTANCE.getIndexWriter();
        writer.deleteDocuments(new Term("id", String.valueOf(id)));
        writer.commit();
    }

    public static void closeReader() throws IOException {
        Reader.INSTANCE.getIndexReader().close();
    }

    public static void closeWriter() throws IOException {
        Writer.INSTANCE.getIndexWriter().close();
    }

    //IndexReader的单例
    enum Reader {
        INSTANCE;
        Directory dir;
        IndexReader indexReader;

        Reader() {
            try {
                dir = FSDirectory.open(Paths.get(DEFAULT_LUCENE_DIR));
                indexReader = DirectoryReader.open(dir);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

        public IndexReader getIndexReader() throws IOException {
            if (DirectoryReader.openIfChanged((DirectoryReader) indexReader) == null) {
                return indexReader;
            } else {
                return indexReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader);
            }
        }
    }

    //IndexWriter的单例
    enum Writer {
        INSTANCE;
        Directory dir;
        IndexWriter indexWriter;

        Writer() {
            try {
                dir = FSDirectory.open(Paths.get(DEFAULT_LUCENE_DIR));
                SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                //索引的打开方式，没有索引文件就新建，有就打开
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                indexWriter = new IndexWriter(dir, config);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

        public IndexWriter getIndexWriter() {
            return indexWriter;
        }
    }

    public static void main(String[] args) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(DEFAULT_LUCENE_DIR));
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        System.out.println(config.getOpenMode());
    }
}
