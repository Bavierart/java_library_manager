import view.MainView;
import view.factory.*;

/**
 * Aplicativo de gerenciamento de biblioteca,
 * argumento --gui para interface visual
 * sem argumentos para interface textual
 * **/
public class Main {
    public static void main(String[] args) {
        boolean gui = args.length > 0 && "gui".equalsIgnoreCase(args[0]);
        AbstractFactory factory = gui ? new GuiFactory() : new TextUiFactory();

        MainView mainView = factory.createMainView();
        mainView.showMenu();
    }
}