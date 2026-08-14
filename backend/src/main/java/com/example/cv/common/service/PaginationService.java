package com.example.cv.common.service;

import java.util.List;
import java.util.function.Predicate;

public final class PaginationService {
    private PaginationService() {
    }

    public static <T> PageResult<T> page(List<T> items, Integer current, Integer pageSize,
                                         Predicate<T> filter) {
        int page = current == null || current < 1 ? 1 : current;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        List<T> filtered = items.stream().filter(filter == null ? value -> true : filter).toList();
        int total = filtered.size();
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);
        return new PageResult<>(page, size, (int) Math.ceil(total / (double) size), total,
                filtered.subList(from, to));
    }

    public record PageResult<T>(int current, int pageSize, int pages, int total, List<T> result) {
        public java.util.Map<String, Object> asMap() {
            return java.util.Map.of(
                    "meta", java.util.Map.of("current", current, "pageSize", pageSize,
                            "pages", pages, "total", total),
                    "result", result);
        }

        public java.util.Map<String, Object> asMapWith(List<?> mappedResult) {
            return java.util.Map.of(
                    "meta", java.util.Map.of("current", current, "pageSize", pageSize,
                            "pages", pages, "total", total),
                    "result", mappedResult);
        }
    }
}
