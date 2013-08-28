package org.framework.jdbc;

public final class Limit {

    private Long firstResults;

    private final Long maxResults;

    private final Long numberPages;

    public Limit(final Long firstResults, final Long maxResults) {
        this.firstResults = firstResults;
        this.maxResults = maxResults;
        numberPages = null;
    }

    public Limit(final Long firstResults,
                 final Long maxResults,
                 final Long numberPages) {
        this.firstResults = firstResults;
        this.maxResults = maxResults;
        this.numberPages = numberPages;
    }

    public Limit add(final int value) {
        if (value > 0)
            firstResults += value;
        else firstResults -= Math.abs(value);
        return this;
    }

    public Long getFirstResults() {
        return firstResults;
    }

    public Long getMaxResults() {
        return maxResults;
    }

    public Long getNumberPages() {
        return numberPages;
    }

    public boolean isFirst() {
        return firstResults.equals(0L);
    }

    public boolean isLast() {
        return Long.valueOf(firstResults + 1).compareTo(numberPages) >= 0;
    }
}
