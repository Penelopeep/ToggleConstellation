package pene.gc.ToggleCons.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.AvatarTalentData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketAvatarUnlockTalentNotify;
import emu.grasscutter.server.packet.send.PacketSceneEntityAppearNotify;
import emu.grasscutter.server.packet.send.PacketUnlockAvatarTalentRsp;
import emu.grasscutter.utils.Position;

import java.util.List;
import java.util.NoSuchElementException;


@Command(label = "toggleconstellation",aliases = {"tc", "togcons, togglecons"},
        usage = "setconstellations [0-6/all]")
public final class Toggleconstellations implements CommandHandler {
    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1){
            if (sender != null) {
                CommandHandler.sendMessage(targetPlayer, "/toggleconstellation, /tc, /togglecons or /togcons [1-6] / all");
            }
            else {
                Grasscutter.getLogger().info("toggleconstellation, tc, togglecons or togcons  [1-6] / all");
            }
            return;
        }
        int constellation;
        String messageSuccess;
        if (args.get(0).equals("all")){
            messageSuccess = AllConstellation(targetPlayer);
        } else {
            try {
                constellation = Integer.parseInt(args.get(0));
                if (constellation > 6 || constellation < 1) {
                    if (sender != null) {
                        CommandHandler.sendMessage(targetPlayer, "Invalid number/All command");
                    } else {
                        Grasscutter.getLogger().info("Invalid number/All command");
                    }
                    return;
                }
                messageSuccess = this.Toggleconstellation(targetPlayer, constellation);
            } catch (NumberFormatException e) {
                if (sender != null) {
                    CommandHandler.sendMessage(targetPlayer, "Use ONLY constellation number");
                } else {
                    Grasscutter.getLogger().info("Use ONLY constellation number");
                }
                return;
            }
        }
        int scene = targetPlayer.getSceneId();
        try {
            Position targetPlayerPos = targetPlayer.getPosition();
            targetPlayer.getWorld().transferPlayerToScene(targetPlayer, 1, targetPlayerPos);
            targetPlayer.getWorld().transferPlayerToScene(targetPlayer, scene, targetPlayerPos);
            targetPlayer.getScene().broadcastPacket(new PacketSceneEntityAppearNotify(targetPlayer));
        }
        catch (NoSuchElementException f){
            if (sender != null) {
                CommandHandler.sendMessage(targetPlayer, "Unknown error, probably caused by removing constellation");
            } else {
                Grasscutter.getLogger().info("Unknown error, probably caused by removing constellation");
            }
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (sender != null) {
            CommandHandler.sendMessage(targetPlayer, messageSuccess);
        }
        else {
            Grasscutter.getLogger().info(messageSuccess);
        }
    }

    private int getConstellation(Avatar avatar, int constellation){
        int talentId = ((avatar.getAvatarId() % 10000000) * 10) + constellation;
        if (avatar.getAvatarId() == 10000006) {
            // Lisa is special in that her talentId starts with 4 instead of 6.
            talentId = 40 + constellation;
        }
        return talentId;
    }
    private String AllConstellation(Player player) {
        Avatar avatar = player.getTeamManager().getCurrentAvatarEntity().getAvatar();
        int cons = getConstellation(avatar, 0);
        List <Integer>list = avatar.getSkillDepot().getTalents();
        list.clear();
        for (int i=1; i<7; i++){
            list.add(cons + i);
            player.sendPacket(new PacketAvatarUnlockTalentNotify(avatar, cons + i));
            player.sendPacket(new PacketUnlockAvatarTalentRsp(avatar, cons + i));
        }
        avatar.recalcConstellations();
        avatar.recalcStats();
        avatar.save();
        return "All const activated, but some constellations may require relog";
    }

    private String Toggleconstellation(Player targetPlayer, int constellation){
        EntityAvatar entity = targetPlayer.getTeamManager().getCurrentAvatarEntity();
        Avatar avatar = entity.getAvatar();
        int consId = getConstellation(avatar, constellation);
        List<Integer> list = avatar.getSkillDepot().getTalents();
        System.out.println(list);
        boolean isAlready = false;
        for (int cons : list){
            if (cons == consId) {
                isAlready = true;
                System.out.println(cons);
                break;
            }
        }
        try{
            String messageSuccess;
            if (!isAlready){
                list.add(consId); //Proud lvl jest prawdopodobnie ustawiony na 0 dlatego wszystkie konstellacje są wpisane ale żadna nie jest odblokowana.
                AvatarTalentData talentData = GameData.getAvatarTalentDataMap().get(consId);
                avatar.unlockConstellation(consId, true);
                avatar.calcConstellation(GameData.getOpenConfigEntries().get(talentData.getOpenConfig()), true);
                messageSuccess = String.format("Successfully activated C%s, but some constellations may require relog",constellation);
            }
            else{
                list.remove((Integer) consId);
                messageSuccess = String.format("Successfully deactivated C%s, but some constellations may require relog", constellation);
            }
            avatar.recalcConstellations();
            avatar.recalcStats(true);
            avatar.save();
            return messageSuccess;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "something is really fucked up if you see this";
    }
}
