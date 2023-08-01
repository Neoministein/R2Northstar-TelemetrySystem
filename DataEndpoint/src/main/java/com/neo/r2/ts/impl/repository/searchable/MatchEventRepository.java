package com.neo.r2.ts.impl.repository.searchable;

import com.neo.r2.ts.api.SearchableRepository;
import com.neo.r2.ts.persistence.searchable.MatchEventSearchable;
import com.neo.util.framework.elastic.api.IndexNamingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MatchEventRepository implements SearchableRepository {

    protected String indexName;

    @Inject
    public void init(IndexNamingService indexNamingService) {
        indexName = indexNamingService.getIndexNamePrefixFromClass(MatchEventSearchable.class, true);
    }

    @Override
    public String getIndexName() {
        return indexName;
    }
}
