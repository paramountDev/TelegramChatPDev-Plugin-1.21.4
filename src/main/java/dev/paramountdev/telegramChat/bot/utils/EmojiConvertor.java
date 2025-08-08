package dev.paramountdev.telegramChat.bot.utils;

import java.util.HashMap;
import java.util.Map;

public class EmojiConvertor {

    private static final Map<String, String> emojiTranslations = new HashMap<>();
    private static final Map<String, String> textCodeToEmoji = new HashMap<>();

    static {
        emojiTranslations.put("😂", "§e laughter §r");
        emojiTranslations.put("❤️", "§c love §r");
        emojiTranslations.put("😍", "§d in love §r");
        emojiTranslations.put("😘", "§c kiss §r");
        emojiTranslations.put("🤣", "§6 can't stop laughing §r");
        emojiTranslations.put("😭", "§b crying §r");
        emojiTranslations.put("😊", "§a joy §r");
        emojiTranslations.put("😉", "§e winking §r");
        emojiTranslations.put("😡", "§4 angry §r");
        emojiTranslations.put("🙏", "§b thank you §r");
        emojiTranslations.put("👍", "§2 ok §r");
        emojiTranslations.put("👎", "§8 not ok §r");
        emojiTranslations.put("🎉", "§5 celebrating §r");
        emojiTranslations.put("🎂", "§d birthday §r");
        emojiTranslations.put("😎", "§9 cool §r");
        emojiTranslations.put("😢", "§b sad §r");
        emojiTranslations.put("😱", "§3 shocked §r");
        emojiTranslations.put("🤔", "§7 thinking §r");
        emojiTranslations.put("🤯", "§5 mind blown §r");
        emojiTranslations.put("🤡", "§d clown §r");
        emojiTranslations.put("💩", "§6 poop §r");
        emojiTranslations.put("🔥", "§c fire §r");
        emojiTranslations.put("💀", "§8 skull §r");
        emojiTranslations.put("😴", "§9 sleeping §r");
        emojiTranslations.put("🤤", "§a drooling §r");
        emojiTranslations.put("😇", "§f angel §r");
        emojiTranslations.put("👀", "§7 watching §r");
        emojiTranslations.put("🥰", "§d adoration §r");
        emojiTranslations.put("😤", "§c huffing §r");
        emojiTranslations.put("😈", "§5 devilish §r");
        emojiTranslations.put("💘", "§d love struck §r");
        emojiTranslations.put("💔", "§4 heartbreak §r");
        emojiTranslations.put("💖", "§d glowing love §r");
        emojiTranslations.put("💋", "§c kiss §r");
        emojiTranslations.put("🤮", "§a vomiting §r");
        emojiTranslations.put("😜", "§e joking §r");
        emojiTranslations.put("😅", "§e nervous laugh §r");
        emojiTranslations.put("😐", "§7 neutral §r");
        emojiTranslations.put("😬", "§8 awkward §r");
        emojiTranslations.put("🤬", "§4 swearing §r");
        emojiTranslations.put("😻", "§d cute cat §r");
        emojiTranslations.put("🤗", "§a hugging §r");
        emojiTranslations.put("🤓", "§b nerd §r");
        emojiTranslations.put("🤫", "§8 shhh §r");
        emojiTranslations.put("👻", "§7 ghost §r");
        emojiTranslations.put("🎃", "§6 pumpkin §r");
        emojiTranslations.put("😷", "§8 sick §r");
        emojiTranslations.put("🤒", "§4 fever §r");
        emojiTranslations.put("🤕", "§6 injury §r");
        emojiTranslations.put("🥶", "§b freezing §r");
        emojiTranslations.put("🥵", "§c hot §r");
        emojiTranslations.put("😳", "§e embarrassed §r");
        emojiTranslations.put("😵", "§8 dizzy §r");
        emojiTranslations.put("🥴", "§d drunk §r");
        emojiTranslations.put("🤠", "§6 cowboy §r");
        emojiTranslations.put("😺", "§e smiling cat §r");
        emojiTranslations.put("🙀", "§3 scared cat §r");
        emojiTranslations.put("😿", "§b crying cat §r");
        emojiTranslations.put("🐶", "§6 dog §r");
        emojiTranslations.put("🐱", "§d cat §r");
        emojiTranslations.put("🐭", "§7 mouse §r");
        emojiTranslations.put("🐹", "§e hamster §r");
        emojiTranslations.put("🐰", "§f bunny §r");
        emojiTranslations.put("🦊", "§6 fox §r");
        emojiTranslations.put("🐻", "§6 bear §r");
        emojiTranslations.put("🐼", "§f panda §r");
        emojiTranslations.put("🐨", "§f koala §r");
        emojiTranslations.put("🐯", "§e tiger §r");
        emojiTranslations.put("🦁", "§6 lion §r");
        emojiTranslations.put("🐮", "§f cow §r");
        emojiTranslations.put("🐷", "§d pig §r");
        emojiTranslations.put("🐸", "§a frog §r");
        emojiTranslations.put("🐵", "§e monkey §r");
        emojiTranslations.put("🙈", "§6 see no evil §r");
        emojiTranslations.put("🙉", "§e hear no evil §r");
        emojiTranslations.put("🙊", "§7 speak no evil §r");
        emojiTranslations.put("💥", "§c explosion §r");
        emojiTranslations.put("💣", "§8 bomb §r");
        emojiTranslations.put("🧨", "§c firecracker §r");
        emojiTranslations.put("⚡", "§e lightning §r");
        emojiTranslations.put("🌪️", "§7 tornado §r");
        emojiTranslations.put("🌈", "§d rainbow §r");
        emojiTranslations.put("☀️", "§e sun §r");
        emojiTranslations.put("🌧️", "§9 rain §r");
        emojiTranslations.put("🌨️", "§f snow §r");
        emojiTranslations.put("❄️", "§b snowflake §r");
        emojiTranslations.put("💧", "§b drop §r");
        emojiTranslations.put("🌊", "§3 wave §r");
        emojiTranslations.put("🌍", "§a earth §r");
        emojiTranslations.put("🌙", "§7 moon §r");


        textCodeToEmoji.put("%laugh%", "😂");
        textCodeToEmoji.put("%love%", "❤️");
        textCodeToEmoji.put("%in_love%", "😍");
        textCodeToEmoji.put("%kiss%", "😘");
        textCodeToEmoji.put("%rofl%", "🤣");
        textCodeToEmoji.put("%cry%", "😭");
        textCodeToEmoji.put("%joy%", "😊");
        textCodeToEmoji.put("%wink%", "😉");
        textCodeToEmoji.put("%angry%", "😡");
        textCodeToEmoji.put("%thank_you%", "🙏");
        textCodeToEmoji.put("%thumbs_up%", "👍");
        textCodeToEmoji.put("%thumbs_down%", "👎");
        textCodeToEmoji.put("%party%", "🎉");
        textCodeToEmoji.put("%birthday%", "🎂");
        textCodeToEmoji.put("%cool%", "😎");
        textCodeToEmoji.put("%sad%", "😢");
        textCodeToEmoji.put("%shock%", "😱");
        textCodeToEmoji.put("%thinking%", "🤔");
        textCodeToEmoji.put("%mind_blown%", "🤯");
        textCodeToEmoji.put("%clown%", "🤡");
        textCodeToEmoji.put("%poop%", "💩");
        textCodeToEmoji.put("%fire%", "🔥");
        textCodeToEmoji.put("%skull%", "💀");
        textCodeToEmoji.put("%sleeping%", "😴");
        textCodeToEmoji.put("%drooling%", "🤤");
        textCodeToEmoji.put("%angel%", "😇");
        textCodeToEmoji.put("%eyes%", "👀");
        textCodeToEmoji.put("%adoration%", "🥰");
        textCodeToEmoji.put("%huff%", "😤");
        textCodeToEmoji.put("%devil%", "😈");
        textCodeToEmoji.put("%love_arrow%", "💘");
        textCodeToEmoji.put("%broken_heart%", "💔");
        textCodeToEmoji.put("%sparkling_heart%", "💖");
        textCodeToEmoji.put("%kiss_mark%", "💋");
        textCodeToEmoji.put("%vomit%", "🤮");
        textCodeToEmoji.put("%tongue_out%", "😜");
        textCodeToEmoji.put("%nervous_laugh%", "😅");
        textCodeToEmoji.put("%neutral%", "😐");
        textCodeToEmoji.put("%awkward%", "😬");
        textCodeToEmoji.put("%swearing%", "🤬");
        textCodeToEmoji.put("%heart_eyes_cat%", "😻");
        textCodeToEmoji.put("%hugging%", "🤗");
        textCodeToEmoji.put("%nerd%", "🤓");
        textCodeToEmoji.put("%shushing%", "🤫");
        textCodeToEmoji.put("%ghost%", "👻");
        textCodeToEmoji.put("%pumpkin%", "🎃");
        textCodeToEmoji.put("%sick%", "😷");
        textCodeToEmoji.put("%thermometer%", "🤒");
        textCodeToEmoji.put("%injured%", "🤕");
        textCodeToEmoji.put("%freezing%", "🥶");
        textCodeToEmoji.put("%hot%", "🥵");
        textCodeToEmoji.put("%embarrassed%", "😳");
        textCodeToEmoji.put("%dazed%", "😵");
        textCodeToEmoji.put("%drunk%", "🥴");
        textCodeToEmoji.put("%cowboy%", "🤠");
        textCodeToEmoji.put("%smiling_cat%", "😺");
        textCodeToEmoji.put("%fearful_cat%", "🙀");
        textCodeToEmoji.put("%crying_cat%", "😿");
        textCodeToEmoji.put("%dog%", "🐶");
        textCodeToEmoji.put("%cat%", "🐱");
        textCodeToEmoji.put("%mouse%", "🐭");
        textCodeToEmoji.put("%hamster%", "🐹");
        textCodeToEmoji.put("%rabbit%", "🐰");
        textCodeToEmoji.put("%fox%", "🦊");
        textCodeToEmoji.put("%bear%", "🐻");
        textCodeToEmoji.put("%panda%", "🐼");
        textCodeToEmoji.put("%koala%", "🐨");
        textCodeToEmoji.put("%tiger%", "🐯");
        textCodeToEmoji.put("%lion%", "🦁");
        textCodeToEmoji.put("%cow%", "🐮");
        textCodeToEmoji.put("%pig%", "🐷");
        textCodeToEmoji.put("%frog%", "🐸");
        textCodeToEmoji.put("%monkey%", "🐵");
        textCodeToEmoji.put("%see_no_evil%", "🙈");
        textCodeToEmoji.put("%hear_no_evil%", "🙉");
        textCodeToEmoji.put("%speak_no_evil%", "🙊");
        textCodeToEmoji.put("%explosion%", "💥");
        textCodeToEmoji.put("%bomb%", "💣");
        textCodeToEmoji.put("%firecracker%", "🧨");
        textCodeToEmoji.put("%lightning%", "⚡");
        textCodeToEmoji.put("%tornado%", "🌪️");
        textCodeToEmoji.put("%rainbow%", "🌈");
        textCodeToEmoji.put("%sun%", "☀️");
        textCodeToEmoji.put("%rain%", "🌧️");
        textCodeToEmoji.put("%snow%", "🌨️");
        textCodeToEmoji.put("%snowflake%", "❄️");
        textCodeToEmoji.put("%drop%", "💧");
        textCodeToEmoji.put("%wave%", "🌊");
        textCodeToEmoji.put("%earth%", "🌍");
        textCodeToEmoji.put("%moon%", "🌙");

    }



    public static String processEmojiMessage(String message) {
        String result = message;
        for (Map.Entry<String, String> entry : emojiTranslations.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static String replaceTextCodesWithEmojis(String message) {
        String result = message;
        for (Map.Entry<String, String> entry : textCodeToEmoji.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

}
