package slipstream.tidy.utils;
/**
 * The TestKit is a utility class whose primary contribution are ASCII art prints.
 * ASCII art prints are useful to provide space between separate log details.
 */
public class TestKit {
    /**
     * dummyChoiceBox(String message, boolean response):
     * A real choice box displays newMessage and gives options "Yes" or "No"
     * Yes: return true
     * No: return false
     * Whereas a dummy choice box will return your specified desiredResponse without prompt.
     */
    public static boolean dummyChoiceBox(String message, boolean response) {
        return response;
    }
    /**
     * printHourGlass()
     * printDevilFace()
     * printZippo()
     * ASCII Art spacers for console debug output.
     */
    public static void printHourGlass() {
        System.out.println("\n" +
                "                            ____\n" +
                "        ____....----''''````    |.\n" +
                ",'''````            ____....----; '.\n" +
                "| __....----''''````         .-.`'. '.\n" +
                "|.-.                .....    | |   '. '.\n" +
                "`| |        ..:::::::::::::::| |   .-;. |\n" +
                " | |`'-;-::::::::::::::::::::| |,,.| |-='\n" +
                " | |   | ::::::::::::::::::::| |   | |\n" +
                " | |   | :::::::::::::::;;;;;| |   | |\n" +
                " | |   | :::::::::;;;2KY2KY2Y| |   | |\n" +
                " | |   | :::::;;Y2KY2KY2KY2KY| |   | |\n" +
                " | |   | :::;Y2Y2KY2KY2KY2KY2| |   | |\n" +
                " | |   | :;Y2KY2KY2KY2KY2K+++| |   | |\n" +
                " | |   | |;2KY2KY2KY2++++++++| |   | |\n" +
                " | |   | | ;++++++++++++++++;| |   | |\n" +
                " | |   | |  ;++++++++++++++;.| |   | |\n" +
                " | |   | |   :++++++++++++:  | |   | |\n" +
                " | |   | |    .:++++++++;.   | |   | |\n" +
                " | |   | |       .:;+:..     | |   | |\n" +
                " | |   | |         ;;        | |   | |\n" +
                " | |   | |      .,:+;:,.     | |   | |\n" +
                " | |   | |    .::::;+::::,   | |   | |\n" +
                " | |   | |   ::::::;;::::::. | |   | |\n" +
                " | |   | |  :::::::+;:::::::.| |   | |\n" +
                " | |   | | ::::::::;;::::::::| |   | |\n" +
                " | |   | |:::::::::+:::::::::| |   | |\n" +
                " | |   | |:::::::::+:::::::::| |   | |\n" +
                " | |   | ::::::::;+++;:::::::| |   | |\n" +
                " | |   | :::::::;+++++;::::::| |   | |\n" +
                " | |   | ::::::;+++++++;:::::| |   | |\n" +
                " | |   |.:::::;+++++++++;::::| |   | |\n" +
                " | | ,`':::::;+++++++++++;:::| |'\"-| |-..\n" +
                " | |'   ::::;+++++++++++++;::| |   '-' ,|\n" +
                " | |    ::::;++++++++++++++;:| |     .' |\n" +
                ",;-'_   `-._===++++++++++_.-'| |   .'  .'\n" +
                "|    ````'''----....___-'    '-' .'  .'\n" +
                "'---....____           ````'''--;  ,'\n" +
                "            ````''''----....____|.'\n"
        );
    }
    /**
     * <p>This is a simple description of the method. . .
     * <a href="http://www.supermanisthegreatest.com">Superman!</a>
     * </p>
     * @param incomingDamage the amount of incoming damage
     * @return the amount of health hero has after attack
     * @see <a href="http://www.link_to_jira/HERO-402">HERO-402</a>
     * @since 1.0
     */
    public static void printDevilFace() {
        System.out.println("\n" +
                ".-._                                                   _,-,\n" +
                "  `._`-._                                           _,-'_,'\n" +
                "     `._ `-._                                   _,-' _,'\n" +
                "        `._  `-._        __.-----.__        _,-'  _,'\n" +
                "           `._   `#===\"\"\"           \"\"\"===#'   _,'\n" +
                "              `._/)  ._               _.  (\\_,'\n" +
                "               )*'     **.__     __.**     '*( \n" +
                "               #  .==..__  \"\"   \"\"  __..==,  # \n" +
                "               #   `\"._(_).       .(_)_.\"'   #\n"
        );
    }
    public static void printZippo() {
        System.out.println("\n" +
                "___    A\n" +
                "| |   {*}\n" +
                "| |  __V__\n" +
                "|_|o_|%%%|0_\n" +
                "   |       |\n" +
                "   |       |\n" +
                "   |_______|\n"
        );
    }
}
