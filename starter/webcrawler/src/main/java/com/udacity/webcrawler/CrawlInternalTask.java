package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.util.concurrent.RecursiveAction;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.time.Clock;

public final class CrawlInternalTask extends RecursiveAction {
    private final String url;
    private final Instant deadline;
    private int maxDepth;
    private Map<String, Integer> counts;
    private Set<String> visitedUrls;
    private final List<Pattern> ignoredUrls;
    private final Clock clock;
    private final PageParserFactory parserFactory;

    public CrawlInternalTask(String url,
     Instant deadline,
     int maxDepth,
     Map<String, Integer> counts,
     Set<String> visitedUrls,
     List<Pattern> ignoredUrls,
     Clock clock,
     PageParserFactory parserFactory) {
        this.url = url;
        this.deadline = deadline;
        this.maxDepth = maxDepth;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
        this.ignoredUrls = ignoredUrls;
        this.clock = clock;
        this.parserFactory = parserFactory;
    }

    @Override
    protected void compute() {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }
        if (visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        PageParser.Result result = parserFactory.get(url).parse();

        for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            counts.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : v + e.getValue());
        }

        List<CrawlInternalTask> subtasks = null;
        for (String link : result.getLinks()) {
            CrawlInternalTask subtask = new CrawlInternalTask(link, deadline, maxDepth - 1, counts, visitedUrls, ignoredUrls, clock, parserFactory);
            subtasks.add(subtask);
        }
        invokeAll(subtasks);
    }
}


