package com.neo.r2.ts.web.rest.dto.outbound;

import java.util.List;

public record HitsDto<T>(List<T> hits) {
}
