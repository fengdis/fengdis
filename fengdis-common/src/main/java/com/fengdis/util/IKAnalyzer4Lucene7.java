package com.fengdis.util;

import org.apache.lucene.analysis.Analyzer;

/**
 * @version 1.0
 * @Descrittion: 因为Analyzer的createComponents方法API改变了需要重新实现分析器
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
public class IKAnalyzer4Lucene7 extends Analyzer {

    private boolean useSmart = false;

    public IKAnalyzer4Lucene7() {
        this(false);
    }

    public IKAnalyzer4Lucene7(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        IKTokenizer4Lucene7 tk = new IKTokenizer4Lucene7(this.useSmart);
        return new TokenStreamComponents(tk);
    }

}