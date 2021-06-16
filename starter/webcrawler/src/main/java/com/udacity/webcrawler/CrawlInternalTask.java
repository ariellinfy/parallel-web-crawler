

public final class CrawlInternalTask extends RecursiveTask<> {
    private final String url;
    private final Instant deadline;
    private final int maxDepth;
    private final Map<String, Integer> counts;
    private final Set<String> visitedUrls;

    public CrawlInternalTask(String url, Instant deadline, int maxDepth, Map<String, Integer> counts, Set<String> visitedUrls) {
        this.url = url;
        this.deadline = deadline;
        this.maxDepth = maxDepth;
        this.counts = counts;
        this.visitedUrls = visitedUrls;
    }

//    @Override
//    protected Long compute() {
//        if (!Files.isDirectory(path)) {
//            return WordCounter.countWordInFile(path, word);
//        }
//        Stream<Path> subpaths;
//        try {
//            subpaths = Files.list(path);
//        } catch (IOException e) {
//            return 0L;
//        }
//        List<CountWordsTask> subtasks =
//                subpaths.map(path -> new CountWordsTask(path, word))
//                        .collect(Collectors.toList());
//        invokeAll(subtasks);
//        return subtasks
//                .stream()
//                .mapToLong(CountWordsTask::getRawResult)
//                .sum();
//    }

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
        if (counts.containsKey(e.getKey())) {
            counts.put(e.getKey(), e.getValue() + counts.get(e.getKey()));
        } else {
            counts.put(e.getKey(), e.getValue());
        }
    }
    for (String link : result.getLinks()) {
        CrawlInternalTask(link, deadline, maxDepth - 1, counts, visitedUrls);
    }
}