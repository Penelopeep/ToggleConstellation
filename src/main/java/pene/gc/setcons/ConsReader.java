package pene.gc.setcons;

import emu.grasscutter.game.player.Player;

import java.util.List;

public class ConsReader{
    public static Integer reader(Player targetPlayer, int constellation) {
        int cons = 0;
        try {
            List<Integer> consSet = targetPlayer.getTeamManager().getCurrentAvatarEntity().getAvatar().getSkillDepot().getTalents();
            for (int consLoop : consSet){
                if (consLoop%10 == constellation){
                    cons = consLoop;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cons;
    }
}