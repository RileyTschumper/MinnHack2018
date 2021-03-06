
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.*;

/*
    This class defines a defensive formation.
 */
public class Defense extends Formation
{
    
    public Defense(Team team)
    {
        super(team);
    }

    //Adds a player and checks that it is above the y-axis, and that it's position
    //is defensive
    @Override
    public void addPlayer(Player player, TeamPositions teamPosition, Point2D.Double location)
    {
        if (location.y>0)
            {
                JOptionPane.showMessageDialog(new JFrame(), "You cannot put a defenseman there!",
                    "Illegal placement", JOptionPane.WARNING_MESSAGE);
                return;
            }
        if (teamPosition.getOffense())
                throw new IllegalArgumentException(
                    "Cannot add defensive player to offensive formation");
        super.addPlayer(player, teamPosition, location);
    }

    public RiskFactor[] getRiskLevels(Offense opposingTeam)
    {
        Set<Entry<Integer,PlayerEntry>> defenseSet = this.getHashMap().entrySet();
        Set<Entry<Integer,PlayerEntry>> offenseSet = opposingTeam.getHashMap().entrySet();
        RiskFactor[] output = new RiskFactor[defenseSet.size()];
        int i=0;
        //Increments through all defensive entries
        for (Entry<Integer,PlayerEntry> defensiveEntry : defenseSet)
        {
            double thisDangerValue=0.0;//defensiveEntry.getValue().getPlayer().riskAgainst();
            //Increments through every entry in the offensive set
            for (Map.Entry<Integer,PlayerEntry> offensiveEntry : offenseSet)
            {
                for (TeamPositions position : offensiveEntry.getValue().getTeamPositions().getRiskPositions())
                {
                    //Checks if the offensive entry contains the current defensive entry in its teampositions list
                    if (position.equals(defensiveEntry.getValue().getTeamPositions()))
                    {
                        double thisPairingDanger=defensiveEntry.getValue().getPlayer().riskAgainst(offensiveEntry.getValue().getPlayer());
                        thisPairingDanger=thisPairingDanger/defensiveEntry.getValue().getLocation().distance(offensiveEntry.getValue().getLocation());
                        thisDangerValue+=thisPairingDanger;
                    }
                }
            }
            output[i]=new RiskFactor(Math.round(thisDangerValue * 10.0)/10.0,defensiveEntry.getValue());
            i++;
        }
        return output;
    }
}
