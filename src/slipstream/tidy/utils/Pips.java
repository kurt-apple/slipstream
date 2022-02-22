package slipstream.tidy.utils;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableRow;
import javafx.util.Callback;
import slipstream.untidy.taskdb.Task;
import javafx.css.PseudoClass;

import java.util.function.Predicate;

public class Pips {
    public static Callback<ListView<Task>, ListCell<Task>> std_factory = param -> new ListCell<Task>() {
        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || item.getNAME() == null) setText(null);
            else setText(item.getNAME());
        }
    };
    public static void disableCSS(TreeTableRow<Task> row, PseudoClass ... x) {
        for(PseudoClass c : x) row.pseudoClassStateChanged(c, false);
    }
    public static void enableCSS(TreeTableRow<Task> row, PseudoClass ... x) {
        for(PseudoClass c : x) row.pseudoClassStateChanged(c, true);
    }
    public static void decideIf(TreeTableRow<Task> row, Predicate<Task> p, PseudoClass ... x) {
        if(p.test(row.getItem())) enableCSS(row, x);
        else disableCSS(row, x);
    }
}
