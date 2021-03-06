import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

class SafetyPlaybookServer {
    private HttpServer server;

    private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private class requestHandler implements HttpHandler {
        
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "text/json"); 
            
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            Map<String, String> q = queryToMap(query);

            String response = "{";

            if(!q.containsKey("r")) {
                response += je("No request!");
            } else {
                response += this.handleRequest(q);
            }

            response += "}";
            //String response = "This is the query: " + query;
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String handleRequest(Map<String,String> q) {
            String response = "";
            String r = q.get("r");

            if(r.equals("getTeams")) {
                response += "\"teams\": [";

                Map<Integer, Team> teams = SafetyPlaybookDB.getInstance().getTeams();
                for(Map.Entry<Integer, Team> entry : teams.entrySet()) {
                    response += "{" + jst("id", entry.getKey().toString()) + ", " + jst("name", entry.getValue().getName()) + "},";
                }

                response = response.substring(0, response.length() - 1);

                response += "]";
            } else if(r.equals("getTeamPlayers")) {
                if(!q.containsKey("t")) {
                    return je("No team specified!");
                }

                int teamID = Integer.parseInt(q.get("t"));
                Team team  = SafetyPlaybookDB.getInstance().getTeam(teamID);
                if(team == null) return je("No such team!");
                ArrayList<Player> players = team.getPlayerList();

                response += "\"players\": [";
                
                for(Player p : players) {
                    response += jst(""+p.getNumber(), p.getName()) + ",";
                }

                response += "]";
            } else if(r.equals("addTeam")) {
                if(!q.containsKey("n")) {
                    return je("No team name specified!");
                }

                int teamID = SafetyPlaybookDB.getInstance().addTeam(new Team(q.get("n")));

                response += jst("id", ""+teamID);
            } else if(r.equals("addPlayer")) {
                if(!q.containsKey("n")) {
                    return je("No player number specified!");
                } if(!q.containsKey("na")) {
                    return je("No player name specified!");
                } if(!q.containsKey("t")) {
                    return je("No team number specified!");
                }

                int playerN       = Integer.parseInt(q.get("n"));
                String playerName = q.get("na");
                int teamID        = Integer.parseInt(q.get("t"));
                
                Player player = new Player();
                player.setNumber(playerN);
                player.setName(playerName);
                if(q.containsKey("age"))    player.setAge(   Integer.parseInt(q.get("age")));
                if(q.containsKey("height")) player.setHeight(Integer.parseInt(q.get("height")));
                if(q.containsKey("weight")) player.setWeight(Integer.parseInt(q.get("weight")));

                SafetyPlaybookDB.getInstance().addPlayer(teamID, player);
            }
            
            else {
                response += je("Unhandled request: " + r);
            }

            return response;
        }

        private String jst(String key, String value) {
            return "\"" + key + "\": \"" + value + "\"";
        }

        private String je(String error) {
            return jst("error", error);
        }
    }

    /**
     * Setup server
     * 
     * @param port Port number to use
     */
    public SafetyPlaybookServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        this.server.createContext("/", new requestHandler());
    }

    public void start() {
        this.server.setExecutor(null);
        this.server.start();
    }
}