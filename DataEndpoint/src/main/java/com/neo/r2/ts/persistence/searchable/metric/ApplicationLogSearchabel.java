package com.neo.r2.ts.persistence.searchable.metric;

import com.neo.util.framework.api.persistence.search.AbstractSearchable;
import com.neo.util.framework.api.persistence.search.IndexPeriod;
import com.neo.util.framework.api.persistence.search.RetentionPeriod;
import com.neo.util.framework.api.persistence.search.SearchableIndex;

/**
 * This class is used for the Retention Strategy because the index are generated via FileBeat
 */
@SearchableIndex(indexName = ApplicationLogSearchabel.INDEX_NAME, indexPeriod = IndexPeriod.DAILY, retentionPeriod = RetentionPeriod.INDEX_BASED)
public class ApplicationLogSearchabel extends AbstractSearchable {

    public static final String INDEX_NAME = "log-application";
}
