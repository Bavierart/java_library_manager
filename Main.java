import model.User;
import view.MainView;
import view.factory.*;

public class Main {
    public static void main(String[] args) {
        boolean gui = args.length > 0 && "gui".equalsIgnoreCase(args[0]);
        UiFactory factory = gui ? new GuiFactory() : new TextUiFactory();

        MainView mainView = factory.createMainView();
        mainView.showMenu();
    }
}