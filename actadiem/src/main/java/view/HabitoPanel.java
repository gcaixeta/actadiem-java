package view;

import controller.HabitoController;
import dao.HabitoDAO;
import model.Habito;
import model.UnidadeFrequencia;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HabitoPanel extends JPanel {
    private JTextField nomeField;
    private JSpinner quantidadeSpinner;
    private JComboBox<UnidadeFrequencia> unidadeCombo;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Habito> habitosList;
    private DefaultListModel<Habito> listModel;
    private HabitoController habitoController;
    private Long editandoId;

    public HabitoPanel() {
        habitoController = new HabitoController(new HabitoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelListagem(), BorderLayout.CENTER);

        carregarHabitos();
    }

    private JPanel criarPainelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Gerenciar H\u00e1bitos");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        nomeField = new JTextField(20);
        quantidadeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        unidadeCombo = new JComboBox<>(UnidadeFrequencia.values());
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Frequ\u00eancia:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JPanel freqPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        freqPanel.add(quantidadeSpinner);
        freqPanel.add(unidadeCombo);
        panel.add(freqPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        panel.add(botoesPanel, gbc);

        salvarButton.addActionListener(e -> salvarHabito());
        cancelarButton.addActionListener(e -> cancelarEdicao());

        return panel;
    }

    private JPanel criarPainelListagem() {
        listModel = new DefaultListModel<>();
        habitosList = new JList<>(listModel);
        habitosList.setCellRenderer(new HabitoListRenderer());

        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        editarButton.addActionListener(e -> editarHabito());
        excluirButton.addActionListener(e -> excluirHabito());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        botoesPanel.add(editarButton);
        botoesPanel.add(excluirButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Meus h\u00e1bitos"));
        panel.add(new JScrollPane(habitosList), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarHabitos() {
        listModel.clear();
        List<Habito> habitos = habitoController.listarTodos();
        for (Habito habito : habitos) {
            listModel.addElement(habito);
        }
    }

    private void editarHabito() {
        Habito selecionado = habitosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um h\u00e1bito para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editandoId = selecionado.getId();
        nomeField.setText(selecionado.getNome());
        quantidadeSpinner.setValue(selecionado.getFrequencia().getQuantidade());
        unidadeCombo.setSelectedItem(selecionado.getFrequencia().getUnidade());
        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
    }

    private void excluirHabito() {
        Habito selecionado = habitosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um h\u00e1bito para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Excluir o h\u00e1bito \"" + selecionado.getNome() + "\"?",
                "Confirmar exclus\u00e3o",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            habitoController.deletar(selecionado.getId());
            carregarHabitos();
        }
    }

    private void cancelarEdicao() {
        editandoId = null;
        nomeField.setText("");
        quantidadeSpinner.setValue(1);
        unidadeCombo.setSelectedIndex(0);
        salvarButton.setText("Salvar");
        cancelarButton.setVisible(false);
    }

    private void salvarHabito() {
        String nome = nomeField.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome \u00e9 obrigat\u00f3rio!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Habito habito = new Habito(nome);
        int quantidade = (int) quantidadeSpinner.getValue();
        UnidadeFrequencia unidade = (UnidadeFrequencia) unidadeCombo.getSelectedItem();
        habito.getFrequencia().setQuantidade(quantidade);
        habito.getFrequencia().setUnidade(unidade);

        if (editandoId != null) {
            habito.setId(editandoId);
            habitoController.atualizar(habito);
            cancelarEdicao();
        } else {
            habitoController.salvar(habito);
            nomeField.setText("");
            quantidadeSpinner.setValue(1);
            unidadeCombo.setSelectedIndex(0);
        }

        carregarHabitos();
    }

    private static class HabitoListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Habito habito) {
                String unidade = switch (habito.getFrequencia().getUnidade()) {
                    case DIA -> "vez(es) por dia";
                    case SEMANA -> "vez(es) por semana";
                    case MES -> "vez(es) por m\u00eas";
                    case ANO -> "vez(es) por ano";
                };
                setText(habito.getNome() + " - " + habito.getFrequencia().getQuantidade() + " " + unidade);
            }
            return c;
        }
    }
}
