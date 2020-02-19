package com.fengdis.component.rpc.elasticsearch.es;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Descrittion: 智能分词
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
public class Analzyer {
	private static Dictionary dictionary;

	static {
		// Configuration cfg = MyDefaultConfig.getInstance();
		Configuration cfg = DefaultConfig.getInstance();
		// 加载词库
		cfg.setUseSmart(true); // 设置智能分词
		Dictionary.initial(cfg);

		dictionary = Dictionary.getSingleton();
	}

	private static void addWord(String word) {
		List<String> ext = new ArrayList<String>();
		ext.add(word);
		dictionary.addWords(ext);
	}

	public static void removeWord(String word) {
		List<String> ext = new ArrayList<String>();
		ext.add(word);
		dictionary.disableWords(ext);
	}

	public static List<String> getWordsList(String text) {
		List<String> retList = new ArrayList<String>();
		// 创建分词对象
		StringReader reader = null;
		Analyzer anal = new IKAnalyzer(true);
		try {
			reader = new StringReader(text);
			// 分词
			TokenStream ts = anal.tokenStream("", reader);
			ts.reset();
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			// 遍历分词数据
			while (ts.incrementToken()) {
				String temp = term.toString();
				if (temp.length() >= 2) {
					retList.add(temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
			anal.close();
		}
		return retList;
	}

	public static void main(String[] args) {
		addWord("中华人");
		removeWord("中华");
		System.out.println(getWordsList("中华人民共和国华人华人"));
		//System.out.println(getWordsList("中华人民共和国人华人华人达斯柯达回来卡里的好啦赫迪拉的话阿好得很阿达"));
	}

	/*public static void main(String[] args) throws IOException {
		String text = "<三峡人家-清江画廊动车2日游>吊脚楼、民歌和土家幺妹、 住商圈酒店 跟";
		// 创建分词对象
		@SuppressWarnings("resource")
		Analyzer anal = new IKAnalyzer(true); // true　用智能分词，false细粒度
		Configuration cfg = DefaultConfig.getInstance();
//        System.out.println(cfg.getMainDictionary()); // 系统默认词库
//        System.out.println(cfg.getQuantifierDicionary());
		Dictionary.initial(cfg);
		List<String> list = new ArrayList<String>();
		list.add("土家幺妹");
		list.add("2日游");
		list.add("三峡");
		list.add("三峡人家");
		Dictionary.getSingleton().addWords(list);
		StringReader reader = new StringReader(text);
		// 分词
		TokenStream ts = anal.tokenStream("", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		// 遍历分词数据
		while (ts.incrementToken()) {
			System.out.print(term.toString() + "|");
		}
		reader.close();
		System.out.println();
	}*/
}
