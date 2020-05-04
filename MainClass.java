package BasicWoodcutter;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Woodcutter", author = "Beebeetee", version = 0.5)

public class MainClass extends AbstractScript{

    Area bankArea = new Area(3207, 3220, 3209, 3216, 2);
    Area treeArea = new Area(3200, 3236, 3193, 3248, 0);
    Tile topStair = new Tile(3206,3209,2);

    @Override
    public void onStart(){
        log("-----STARTING SCRIPT BY BEEBEETEE");
    }


    @Override
    public int onLoop(){

        if (!getInventory().isFull()){
            if(treeArea.contains(getLocalPlayer())){
                chopTree("Tree");
            } else if(getLocalPlayer().getTile().distance(topStair) > 5){
                 if (getWalking().walk(topStair)) {
                     GameObject stairs = getGameObjects().closest("Staircase");
                     if(stairs != null){
                         stairs.interact("Climb-down");

                         sleepUntil(() -> getLocalPlayer().getTile().getZ() == 1, Calculations.random(1000,3000));
                     }
                 }
//                if(getLocalPlayer().getTile().getZ() == 1) {
//                    GameObject stairs = getGameObjects().closest("Staircase");
//                    if (stairs != null) {
//                        stairs.interact("Climb-down");
//                        System.out.println("Hi");
//                        sleepUntil(() -> getLocalPlayer().getTile().getZ() == 0, Calculations.random(1000, 3000));
//                    }
//                }
            }

            else if (getWalking().walk(treeArea.getRandomTile())) {
                    sleep(Calculations.random(3000,6000));
            }
        }

        if(getInventory().isFull()){
            if(bankArea.contains(getLocalPlayer())){
               bank();
            } else {
                if (getWalking().walk(bankArea.getRandomTile())){
                    sleep(Calculations.random(3000,6000));
                }
            }
        }

        return Calculations.random(3000,4000);
    }


    @Override
    public void onExit(){
        log(" ||| Powering down ||| ");
    }

    @Override
    public void onPaint(Graphics graphics){
    }

    private void chopTree(String nameOfTree){     /// Name specific \\\
        GameObject tree = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(nameOfTree));
            if(tree != null && tree.interact("Chop down")){
                int countLog = getInventory().count("Logs");
                sleepUntil(() -> getInventory().count("Logs") > countLog, Calculations.random(8000,12000));
            }
        }

    private void bank(){
        NPC banker = getNpcs().closest(npc -> npc != null && npc.getName().equals("Banker"));
        if(banker != null && banker.interact("Bank")){
            if( sleepUntil(() -> getBank().isOpen(), Calculations.random(5000,9000))){
                if(getBank().depositAllExcept(item -> item != null && item.getName().contains("axe"))){
                    if(sleepUntil(() -> !getInventory().isFull(), Calculations.random(4000,8000))){
                        if(getBank().close()){
                            sleepUntil(() -> !getBank().isOpen(),Calculations.random(4000,8000) );
                        }
                    }
                }
            }
        }
    }
}
