/* *****************************************************************************
 *  Compilation: javac-algs4 BaseballElimination.java
 *  Execution: java-algs4 BaseballElimination teams4.txt
 *  Description:
 *  https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
    private final int numberOfTeams;
    private ArrayList<String> teamID;
    private int[][] teamWL;
    private int[][] schedule;
    private int s; // FlowNetwork source
    private int t; // FlowNetwork sink
    private ArrayList<String> rTeams;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        this.numberOfTeams = in.readInt();

        // Team Index to Name
        teamID = new ArrayList<String>();

        // Team[i] Win[0] Loss[1] Left[2]
        teamWL = new int[numberOfTeams][3];

        // Schedule
        schedule = new int[numberOfTeams][numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            teamID.add(i, in.readString());

            // Fill in record data table
            for (int j = 0; j < 3; j++) {
                teamWL[i][j] = in.readInt();
            }

            // Fill in schedule
            for (int k = 0; k < numberOfTeams; k++) {
                schedule[i][k] = in.readInt();
            }

        }
    }

    // number of teams
    public int numberOfTeams() {
        return this.numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return this.teamID;
    }

    // number of wins for given team
    public int wins(String team) {
        // Check input to method
        validTeam(team);

        int id = teamID.indexOf(team);
        return teamWL[id][0];
    }

    // number of losses for given team
    public int losses(String team) {
        // Check input to method
        validTeam(team);

        int id = teamID.indexOf(team);
        return teamWL[id][1];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        // Check input to method
        validTeam(team);

        int id = teamID.indexOf(team);
        return teamWL[id][2];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        // Check input to method
        validTeams(team1, team2);

        int teamId1 = teamID.indexOf(team1);
        int teamId2 = teamID.indexOf(team2);
        return schedule[teamId1][teamId2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        // Check input to method
        validTeam(team);

        // Erase any previous method calls
        rTeams = new ArrayList<>();

        int id = teamID.indexOf(team);

        // Trivial Elimination
        int maxWins = teamWL[id][0] + teamWL[id][2];
        for (int i = 0; i < numberOfTeams; i++) {
            if (teamWL[i][0] > maxWins) {
                return true;
            }
        }

        // Nontrivial Elimination
        FlowNetwork network = createFFNetwork(id);

        // Perform Ford Fulkerson alg and check source edges.
        FordFulkerson ff = new FordFulkerson(network, s, t);

        for (FlowEdge edge : network.adj(s)) {
            if (edge.flow() < edge.capacity()) {
                return true;
            }
        }

        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        // Check input to method
        validTeam(team);

        // Erase any previous method calls
        rTeams = new ArrayList<>();

        int id = teamID.indexOf(team);

        // Trivial Elimination
        int maxGames = teamWL[id][0] + teamWL[id][2];
        for (int i = 0; i < numberOfTeams; i++) {
            if (teamWL[i][0] > maxGames) {
                rTeams.add(teamID.get(i));
            }
        }

        // Nontrivial Elimination
        FlowNetwork network = createFFNetwork(id);

        // Perform Ford Fulkerson alg and check source edges.
        FordFulkerson ff = new FordFulkerson(network, s, t);

        for (int i = 0; i < numberOfTeams; i++) {
            if (ff.inCut(i)) {
                rTeams.add(teamID.get(i));
            }
        }

        if (!rTeams.isEmpty()) {
            return rTeams;
        }
        else {
            return null;
        }
    }

    private FlowNetwork createFFNetwork(int id) {
        int teams = numberOfTeams;
        s = teams;
        t = teams + 1;
        int gameVertix = teams + 2;
        Set<FlowEdge> edges = new HashSet<>();
        int maxWins = teamWL[id][0] + teamWL[id][2];

        // Create edges in FlowNetwork
        for (int i = 0; i < teams; i++) {  // team 1
            if (i == id || rTeams.contains(teamID.get(i))) {
                continue;
            }

            for (int j = 0; j < i; j++) {  // team 2
                if (schedule[i][j] == 0 || j == id || rTeams.contains(teamID.get(j))) {
                    continue;
                }
                edges.add(new FlowEdge(s, gameVertix, schedule[i][j]));
                edges.add(new FlowEdge(gameVertix, i, Double.POSITIVE_INFINITY));
                edges.add(new FlowEdge(gameVertix, j, Double.POSITIVE_INFINITY));
                gameVertix++;
            }
            edges.add(new FlowEdge(i, t, maxWins - teamWL[i][0]));
        }

        // Create FlowNetwork and add edges
        FlowNetwork network = new FlowNetwork(gameVertix);
        for (FlowEdge edge : edges) {
            network.addEdge(edge);
        }

        return network;
    }

    private void validTeam(String team) {
        if (!teamID.contains(team)) {
            throw new IllegalArgumentException("Invalid team provided");
        }
    }

    private void validTeams(String team1, String team2) {
        if (!teamID.contains(team1) || !teamID.contains(team2)) {
            throw new IllegalArgumentException("Invalid team provided");
        }
    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
