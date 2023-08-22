package com.neo.r2.ts.impl.rss;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record RssItem(
        String id,
        String title,
        @JsonProperty("content_text")
        String contentText,
        @JsonProperty("date_published")
        Instant datePublished,
        String icon) {
}
