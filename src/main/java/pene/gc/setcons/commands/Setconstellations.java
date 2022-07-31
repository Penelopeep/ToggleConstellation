package pene.gc.setcons.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketSceneEntityAppearNotify;
import emu.grasscutter.utils.Position;
import pene.gc.setcons.ConsReader;
import pene.gc.setcons.VersionSupportHelper;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@Command(label = "setconstellations",aliases = {"sc", "setcons"},
        usage = "setconstellations [0-6]")
public final class Setconstellations implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1){
            if (sender != null) {
                CommandHandler.sendMessage(targetPlayer, "/setconstellation or /sc or /setcons [1-6]");
            }
            else {
                Grasscutter.getLogger().info("setconstellation or sc or setcons [1-6]");
            }
            return;
        }
        int constellation = Integer.parseInt(args.get(0));
        if (constellation > 6 || constellation < 1){
            if (sender != null) {
                CommandHandler.sendMessage(targetPlayer, "Invalid constellation");
            }
            else {
                Grasscutter.getLogger().info("Invalid constellation");
            }
            return;
        }
        EntityAvatar entity = targetPlayer.getTeamManager().getCurrentAvatarEntity();
        Avatar avatar = entity.getAvatar();
        String avatarName = avatar.getAvatarData().getName();
        int consId = ConsReader.reader(targetPlayer, avatarName, constellation);
        Set<Integer> list = avatar.getTalentIdList();
        boolean isAlready = false;
        for (int cons : list){
            if (cons == consId) {
                isAlready = true;
                break;
            }
        }
        int highestconst = 0;
        try{
            String messageSuccess = "";
            if (!isAlready){
                avatar.getTalentIdList().add(consId);
                list.add(consId);
                highestconst = Collections.max(list)%10;
                avatar.setCoreProudSkillLevel(highestconst);
                messageSuccess = String.format("Successfully activated C%s, but some constellation may require relog",constellation);
            }
            else{
                avatar.getTalentIdList().remove(consId);
                Set<Integer> newlist = avatar.getTalentIdList();
                highestconst = Collections.max(newlist)%10;
                avatar.setCoreProudSkillLevel(highestconst);
                messageSuccess = String.format("Successfully deactivated C%s, but some constellation may require relog", constellation);

            }
            avatar.recalcStats();
            avatar.save();
            int scene = targetPlayer.getSceneId();
            try {
                Position targetPlayerPos = (Position) VersionSupportHelper.getPositionMethod().invoke(targetPlayer);
                targetPlayer.getWorld().transferPlayerToScene(targetPlayer, 1, targetPlayerPos);
                targetPlayer.getWorld().transferPlayerToScene(targetPlayer, scene, targetPlayerPos);
                targetPlayer.getScene().broadcastPacket(new PacketSceneEntityAppearNotify(targetPlayer));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            if (sender != null) {
                CommandHandler.sendMessage(targetPlayer, messageSuccess);
            }
            else {
                Grasscutter.getLogger().info(messageSuccess);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
