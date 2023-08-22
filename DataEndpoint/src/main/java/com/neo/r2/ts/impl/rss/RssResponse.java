package com.neo.r2.ts.impl.rss;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collection;

public record RssResponse(
        @JsonUnwrapped
        RssHeader rssHeader,
        Collection<RssItem> items
) {
}
