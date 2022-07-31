package pene.gc.setcons;

import com.google.gson.JsonParser;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.player.Player;

import java.io.*;

public class ConsReader{
    public static Integer reader(Player targetPlayer, String avatarName, int constellation) {
        ClassLoader classLoader = SetCons.getInstance().getClass().getClassLoader();
        int cons;
        int consId = 0;
        try (InputStream inputStream = classLoader.getResourceAsStream("Constellations.json");
             InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader)) {
            if (avatarName.equals("PlayerBoy") || avatarName.equals("PlayerGirl")){
                int elementType = Grasscutter.getGameServer().getPlayerByUid(targetPlayer.getUid()).getTeamManager().getCurrentAvatarEntity().getAvatar().getSkillDepotId()%100;//Ugly af but it's only way this works
                String element = "";
                switch (elementType) {
                    case 4 -> element = "Anemo";
                    case 6 -> element = "Geo";
                    case 7 -> element = "Electro";
                    case 8 -> element = "Dendro";
                }
                cons = Integer.parseInt(new JsonParser().parse(reader).getAsJsonObject().get(avatarName).getAsJsonObject().get(element).toString());
            }
            else {
                cons = Integer.parseInt(new JsonParser().parse(reader).getAsJsonObject().get(avatarName).toString());
            }
            consId = cons + constellation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consId;
    }
}