import javafx.util.Pair;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class Grader {

    private static Map<String, Integer> POINTS = new HashMap<>();

    private static int FULL;

    static {
        POINTS.put(BibleScholarTest.class.getSimpleName(), 4);
        POINTS.put(MonasteryTest.class.getSimpleName(), 5);
        POINTS.put(KnightsTemplarBankTest.class.getSimpleName(), 7);
        POINTS.put(MissionTripTest.class.getSimpleName(), 7);

        FULL = POINTS.values().stream().reduce(0, Integer::sum);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            switch (args[0].toUpperCase().charAt(0)) {
                case 'C':
                    GitHub github = GitHub.connect();
                    GHRepository rep = github.getRepository("ny83427/itec597-mid-term");
                    rep.listForks().asList().forEach(Grader::clone);
                    break;
                case 'G':
                    final long now = System.currentTimeMillis();
                    int score = execute();
                    double percentage = score * 100d / FULL;
                    System.out.printf("%d/%d(%.1f%%) graded in %.2fS%n", score, FULL,
                        percentage, (System.currentTimeMillis() - now) / 1000.0);
                    break;
                default:
                    break;
            }
        } else {
            GitHub github = GitHub.connect();
            GHRepository rep = github.getRepository("ny83427/itec597-mid-term");
            PagedIterable<GHRepository> forks = rep.listForks();
            List<Pair<String, Integer>> list = new ArrayList<>(rep.getForks());
            for (GHRepository gp : forks) {
                list.add(process(gp));
            }

            System.out.println(list.size() + " students graded successfully!");
            list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
            for (Pair<String, Integer> p : list) {
                if (p == null) continue;
                double percentage = p.getValue() * 100d / FULL;
                System.out.printf("%s: %d/%d(%.1f%%)%n", p.getKey(), p.getValue(), FULL, percentage);
            }
        }
    }

    private static void clone(GHRepository rep) {
        String student = rep.getOwnerName();
        File file = new File("tmp", student);
        Path path = file.toPath();
        try {
            final long now = System.currentTimeMillis();
            final String url = rep.getHttpTransportUrl();
            if (Files.exists(path) && Files.isDirectory(path)) {
                try {
                    Git.open(file).pull().call();
                    System.out.printf("%s pulled in %.2fS%n", url, (System.currentTimeMillis() - now) / 1000.0);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Files.walk(path).sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
            }
            Git.cloneRepository().setURI(url)
                .setDirectory(path.toFile()).call();
            System.out.printf("%s cloned in %.2fS%n", url, (System.currentTimeMillis() - now) / 1000.0);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Pair<String, Integer> process(GHRepository gp) {
        String student = gp.getOwnerName();
        File file = new File("tmp", student);
        try {
            final long now = System.currentTimeMillis();
            InvocationRequest request = new DefaultInvocationRequest();
            request.setBaseDirectory(file);
            request.setGoals(Arrays.asList("clean", "install"));

            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                result.getExecutionException().printStackTrace();
                return null;
            }
            System.out.printf("Built %s's project successfully in %.2fS%n",
                student, (System.currentTimeMillis() - now) / 1000.0);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
            return null;
        }

        final long now = System.currentTimeMillis();
        int score = execute();
        double percentage = score * 100d / FULL;
        System.out.printf("%s: %d/%d(%.1f%%).", student, score, FULL, percentage);
        System.out.printf("Time Spent: %.2fS%n", (System.currentTimeMillis() - now) / 1000.0);
        return new Pair<>(student, score);
    }

    private static int execute() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClass(BibleScholarTest.class),
                selectClass(MonasteryTest.class),
                selectClass(KnightsTemplarBankTest.class),
                selectClass(MissionTripTest.class)
            ).build();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(testPlan);

        TestExecutionSummary summary = listener.getSummary();
        int total = FULL;
        List<Failure> failures = summary.getFailures();
        for (Failure failure : failures) {
            MethodSource ms = (MethodSource) failure.getTestIdentifier().getSource().orElse(null);
            if (ms == null)
                throw new IllegalStateException("Cannot located test for failure: " + failure);
            int score = POINTS.get(ms.getClassName());
            System.out.printf("%s#%s failed -> %s%n", ms.getClassName(), ms.getMethodName(),
                failure.getException());
            total -= score;
        }
        return total;
    }

}
