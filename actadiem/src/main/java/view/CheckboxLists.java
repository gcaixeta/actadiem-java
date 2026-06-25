package view;

import javax.swing.*;
import java.awt.*;

/**
 * Converts a multi-select {@link JList} into a checkbox list where a plain click
 * toggles each item on/off, making multi-selection obvious to the user.
 *
 * <p>The list's public API is preserved: {@code getSelectedValuesList()} and
 * {@code setSelectedIndices(...)} keep working, so callers' save/edit logic is unchanged.
 *
 * <p>Call {@link #enable(JList)} <em>after</em> setting the list's cell renderer and
 * selection mode, since the checkbox renderer delegates to the existing renderer for text.
 */
public final class CheckboxLists {

    private CheckboxLists() {
    }

    public static void enable(JList<?> list) {
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setSelectionModel(new ToggleSelectionModel());

        ListCellRenderer<Object> base = castRenderer(list.getCellRenderer());
        list.setCellRenderer(new CheckboxRenderer(base));
    }

    @SuppressWarnings("unchecked")
    private static ListCellRenderer<Object> castRenderer(ListCellRenderer<?> renderer) {
        return (ListCellRenderer<Object>) renderer;
    }

    /** A plain click ({@code setSelectionInterval}) toggles instead of replacing. */
    private static class ToggleSelectionModel extends DefaultListSelectionModel {
        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (isSelectedIndex(index0)) {
                super.removeSelectionInterval(index0, index1);
            } else {
                super.addSelectionInterval(index0, index1);
            }
        }
    }

    /** Renders each row as a checkbox, delegating to the base renderer for the row text. */
    private static class CheckboxRenderer extends JCheckBox implements ListCellRenderer<Object> {
        private final ListCellRenderer<Object> base;

        CheckboxRenderer(ListCellRenderer<Object> base) {
            this.base = base;
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = base.getListCellRendererComponent(list, value, index, false, cellHasFocus);
            setText(c instanceof JLabel label ? label.getText() : String.valueOf(value));
            setComponentOrientation(list.getComponentOrientation());
            setFont(list.getFont());
            setEnabled(list.isEnabled());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setSelected(isSelected);
            return this;
        }
    }
}
