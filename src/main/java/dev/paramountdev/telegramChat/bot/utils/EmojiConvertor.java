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


        textCodeToEmoji.put("%laugh%", "😂");             // смех
        textCodeToEmoji.put("%love%", "❤️");              // любовь
        textCodeToEmoji.put("%in_love%", "😍");           // влюблен
        textCodeToEmoji.put("%kiss%", "😘");              // поцелуй
        textCodeToEmoji.put("%rofl%", "🤣");              // ржунимагу
        textCodeToEmoji.put("%cry%", "😭");               // плачу
        textCodeToEmoji.put("%joy%", "😊");               // радость
        textCodeToEmoji.put("%wink%", "😉");              // подмигиваю
        textCodeToEmoji.put("%angry%", "😡");             // злюсь
        textCodeToEmoji.put("%thank_you%", "🙏");         // спасибо
        textCodeToEmoji.put("%thumbs_up%", "👍");         // ок
        textCodeToEmoji.put("%thumbs_down%", "👎");       // не ок
        textCodeToEmoji.put("%party%", "🎉");              // праздную
        textCodeToEmoji.put("%birthday%", "🎂");           // праздник
        textCodeToEmoji.put("%cool%", "😎");               // крутой
        textCodeToEmoji.put("%sad%", "😢");                // грущу
        textCodeToEmoji.put("%shock%", "😱");              // в шоке
        textCodeToEmoji.put("%thinking%", "🤔");           // думаю
        textCodeToEmoji.put("%mind_blown%", "🤯");         // взорван мозг
        textCodeToEmoji.put("%clown%", "🤡");              // клоун
        textCodeToEmoji.put("%poop%", "💩");               // какашка
        textCodeToEmoji.put("%fire%", "🔥");               // огонь
        textCodeToEmoji.put("%skull%", "💀");              // череп
        textCodeToEmoji.put("%sleeping%", "😴");           // сплю
        textCodeToEmoji.put("%drooling%", "🤤");           // слюни
        textCodeToEmoji.put("%angel%", "😇");              // ангел
        textCodeToEmoji.put("%eyes%", "👀");                // смотрю
        textCodeToEmoji.put("%adoration%", "🥰");          // восторг
        textCodeToEmoji.put("%huff%", "😤");                // фыркаю
        textCodeToEmoji.put("%devil%", "😈");               // дьявол
        textCodeToEmoji.put("%love_arrow%", "💘");          // влюблен
        textCodeToEmoji.put("%broken_heart%", "💔");        // разбито сердце
        textCodeToEmoji.put("%sparkling_heart%", "💖");     // сияющая любовь
        textCodeToEmoji.put("%kiss_mark%", "💋");           // поцелуй
        textCodeToEmoji.put("%vomit%", "🤮");                // рыгаю
        textCodeToEmoji.put("%tongue_out%", "😜");          // шучу
        textCodeToEmoji.put("%nervous_laugh%", "😅");       // нервный смех
        textCodeToEmoji.put("%neutral%", "😐");              // нейтрал
        textCodeToEmoji.put("%awkward%", "😬");              // неловко
        textCodeToEmoji.put("%swearing%", "🤬");             // ругаюсь
        textCodeToEmoji.put("%heart_eyes_cat%", "😻");      // мимими
        textCodeToEmoji.put("%hugging%", "🤗");              // обнимаю
        textCodeToEmoji.put("%nerd%", "🤓");                 // умник
        textCodeToEmoji.put("%shushing%", "🤫");             // тише
        textCodeToEmoji.put("%ghost%", "👻");                // привидение
        textCodeToEmoji.put("%pumpkin%", "🎃");              // тыква
        textCodeToEmoji.put("%sick%", "😷");                 // болею
        textCodeToEmoji.put("%thermometer%", "🤒");          // температура
        textCodeToEmoji.put("%injured%", "🤕");              // травма
        textCodeToEmoji.put("%freezing%", "🥶");             // замерз
        textCodeToEmoji.put("%hot%", "🥵");                   // жарко
        textCodeToEmoji.put("%embarrassed%", "😳");          // смущен
        textCodeToEmoji.put("%dazed%", "😵");                 // ошарашен
        textCodeToEmoji.put("%drunk%", "🥴");                 // пьяный
        textCodeToEmoji.put("%cowboy%", "🤠");                // ковбой
        textCodeToEmoji.put("%smiling_cat%", "😺");           // улыбающийся кот
        textCodeToEmoji.put("%fearful_cat%", "🙀");           // испуганный кот
        textCodeToEmoji.put("%crying_cat%", "😿");            // плачущий кот
        textCodeToEmoji.put("%dog%", "🐶");                    // собака
        textCodeToEmoji.put("%cat%", "🐱");                    // кошка
        textCodeToEmoji.put("%mouse%", "🐭");                  // мышка
        textCodeToEmoji.put("%hamster%", "🐹");                // хомяк
        textCodeToEmoji.put("%rabbit%", "🐰");                 // кролик
        textCodeToEmoji.put("%fox%", "🦊");                    // лиса
        textCodeToEmoji.put("%bear%", "🐻");                   // медведь
        textCodeToEmoji.put("%panda%", "🐼");                  // панда
        textCodeToEmoji.put("%koala%", "🐨");                  // коала
        textCodeToEmoji.put("%tiger%", "🐯");                  // тигр
        textCodeToEmoji.put("%lion%", "🦁");                   // лев
        textCodeToEmoji.put("%cow%", "🐮");                    // корова
        textCodeToEmoji.put("%pig%", "🐷");                    // свинья
        textCodeToEmoji.put("%frog%", "🐸");                   // лягушка
        textCodeToEmoji.put("%monkey%", "🐵");                 // обезьяна
        textCodeToEmoji.put("%see_no_evil%", "🙈");            // не вижу
        textCodeToEmoji.put("%hear_no_evil%", "🙉");           // не слышу
        textCodeToEmoji.put("%speak_no_evil%", "🙊");          // молчу
        textCodeToEmoji.put("%explosion%", "💥");              // взрыв
        textCodeToEmoji.put("%bomb%", "💣");                   // бомба
        textCodeToEmoji.put("%firecracker%", "🧨");            // петарда
        textCodeToEmoji.put("%lightning%", "⚡");               // молния
        textCodeToEmoji.put("%tornado%", "🌪️");               // торнадо
        textCodeToEmoji.put("%rainbow%", "🌈");                 // радуга
        textCodeToEmoji.put("%sun%", "☀️");                    // солнце
        textCodeToEmoji.put("%rain%", "🌧️");                   // дождь
        textCodeToEmoji.put("%snow%", "🌨️");                   // снег
        textCodeToEmoji.put("%snowflake%", "❄️");              // снежинка
        textCodeToEmoji.put("%drop%", "💧");                    // капля
        textCodeToEmoji.put("%wave%", "🌊");                    // волна
        textCodeToEmoji.put("%earth%", "🌍");                   // земля
        textCodeToEmoji.put("%moon%", "🌙");                    // луна

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
