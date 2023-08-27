package com.neo.r2.ts.impl.match.result;

import java.util.Arrays;
import java.util.Objects;

public record MatchResultRequestParam(String[] tags, int maxResult, int page) {
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResultRequestParam that = (MatchResultRequestParam) o;
        return maxResult == that.maxResult && page == that.page && Arrays.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(maxResult, page);
        result = 31 * result + Arrays.hashCode(tags);
        return result;
    }
}
