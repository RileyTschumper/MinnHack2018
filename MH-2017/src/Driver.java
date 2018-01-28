import java.awt.geom.Point2D;
import java.util.*;
public class Driver
{

	public static void main(String[] args)
	{
		Team myTeam;

		myTeam= new Team("Jarvis Knights");

		Player mitchell;

		Player riley;

		Player tim;

		Player pete;

		mitchell = new Player();

		riley = new Player();

		tim = new Player();

		pete = new Player();

		Injury mInjury;

		Injury rInjury;

		Injury tInjury;

		Injury pInjury;

		mInjury = new Injury(21,1,2018, 0); // head injury

		rInjury = new Injury(2,1,2018, 1); // torso injury

		tInjury = new Injury(3,1,2018, 2); // arm injury

		pInjury = new Injury(4,1,2018, 3); // leg injury

		mitchell.addInjury(mInjury);

		riley.addInjury(rInjury);

		tim.addInjury(tInjury);

		pete.addInjury(pInjury);

		pete.setWeight(350);

		tim.setWeight(170);

		riley.setWeight(185);

		mitchell.setWeight(100);

		pete.setHeight(60);

		tim.setHeight(70);

		mitchell.setHeight(50);

		riley.setHeight(63);
                
                myTeam.addPlayer(mitchell);
                myTeam.addPlayer(riley);
                myTeam.addPlayer(tim);
                myTeam.addPlayer(pete);

		Offense offense = new Offense(myTeam);
                offense.addPlayer(pete, TeamPositions.OFFENSIVE_LINE, new Point2D.Double(0.0,3.0));
                offense.addPlayer(tim, TeamPositions.QUARTERBACK, new Point2D.Double(0.0,0.0));
                offense.addPlayer(mitchell, TeamPositions.OFFENSIVE_LINE, new Point2D.Double(6.0,3.0));
                
                System.out.println(offense.getPlayer(tim.getNumber()));

		System.out.println(mitchell.riskAgainst(riley));
		//System.out.println(riley.riskFactorAgainst());

		//System.out.println(tim.riskFactorAgainst());

		//System.out.println(pete.riskFactor());





	}
}