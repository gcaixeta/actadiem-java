package view;

import controller.TarefaController;
import dao.TarefaDAO;
import model.Prioridade;
import model.Tarefa;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TarefaPanel extends JPanel {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JComboBox<Prioridade> prioridadeCombo;
    private JCheckBox concluidaCheck;
    private JSpinner dataLimiteSpinner;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Tarefa> tarefasList;
    private DefaultListModel<Tarefa> listModel;
    private TarefaController tarefaController;
    private Long editandoId;

    public TarefaPanel() {
        tarefaController = new TarefaController(new TarefaDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelListagem(), BorderLayout.CENTER);

        carregarTarefas();
    }

    private JPanel criarPainelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Gerenciar Tarefas");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        tituloField = new JTextField(20);
        descricaoField = new JTextArea(5, 20);
        prioridadeCombo = new JComboBox<>(Prioridade.values());
        concluidaCheck = new JCheckBox("Conclu\u00edda");
        dataLimiteSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dataLimiteSpinner, "dd/MM/yyyy");
        dataLimiteSpinner.setEditor(dateEditor);
        dataLimiteSpinner.setValue(new Date());
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("T\u00edtulo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tituloField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(new JLabel("Descri\u00e7\u00e3o:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descricaoField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Prioridade:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(prioridadeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Data limite:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(dataLimiteSpinner, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(concluidaCheck, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        panel.add(botoesPanel, gbc);

        salvarButton.addActionListener(e -> salvarTarefa());
        cancelarButton.addActionListener(e -> cancelarEdicao());

        return panel;
    }

    private JPanel criarPainelListagem() {
        listModel = new DefaultListModel<>();
        tarefasList = new JList<>(listModel);
        tarefasList.setCellRenderer(new TarefaListRenderer());

        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        editarButton.addActionListener(e -> editarTarefa());
        excluirButton.addActionListener(e -> excluirTarefa());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        botoesPanel.add(editarButton);
        botoesPanel.add(excluirButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Minhas tarefas"));
        panel.add(new JScrollPane(tarefasList), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarTarefas() {
        listModel.clear();
        List<Tarefa> tarefas = tarefaController.listarTodos();
        for (Tarefa tarefa : tarefas) {
            listModel.addElement(tarefa);
        }
    }

    private void editarTarefa() {
        Tarefa selecionado = tarefasList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editandoId = selecionado.getId();
        tituloField.setText(selecionado.getTitulo());
        descricaoField.setText(selecionado.getDescricao());
        prioridadeCombo.setSelectedItem(selecionado.getPrioridade());
        concluidaCheck.setSelected(selecionado.isConcluida());
        if (selecionado.getDataLimite() != null) {
            dataLimiteSpinner.setValue(selecionado.getDataLimite());
        }
        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
    }

    private void excluirTarefa() {
        Tarefa selecionado = tarefasList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Excluir a tarefa \"" + selecionado.getTitulo() + "\"?",
                "Confirmar exclus\u00e3o",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tarefaController.deletar(selecionado.getId());
            carregarTarefas();
        }
    }

    private void cancelarEdicao() {
        editandoId = null;
        tituloField.setText("");
        descricaoField.setText("");
        prioridadeCombo.setSelectedIndex(0);
        concluidaCheck.setSelected(false);
        dataLimiteSpinner.setValue(new Date());
        salvarButton.setText("Salvar");
        cancelarButton.setVisible(false);
    }

    private void salvarTarefa() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O t\u00edtulo \u00e9 obrigat\u00f3rio!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        tarefa.setPrioridade((Prioridade) prioridadeCombo.getSelectedItem());
        tarefa.setConcluida(concluidaCheck.isSelected());
        tarefa.setDataLimite((Date) dataLimiteSpinner.getValue());

        if (editandoId != null) {
            tarefa.setId(editandoId);
            tarefaController.atualizar(tarefa);
            cancelarEdicao();
        } else {
            tarefaController.salvar(tarefa);
            tituloField.setText("");
            descricaoField.setText("");
            prioridadeCombo.setSelectedIndex(0);
            concluidaCheck.setSelected(false);
            dataLimiteSpinner.setValue(new Date());
        }

        carregarTarefas();
    }

    private static class TarefaListRenderer extends DefaultListCellRenderer {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tarefa tarefa) {
                String prioridadeLabel = switch (tarefa.getPrioridade()) {
                    case BAIXA -> "Baixa";
                    case MEDIA -> "M\u00e9dia";
                    case GRANDE -> "Grande";
                };
                String data = tarefa.getDataLimite() != null ? dateFormat.format(tarefa.getDataLimite()) : "Sem data";
                String concluida = tarefa.isConcluida() ? "[OK] " : "[  ] ";
                setText(concluida + tarefa.getTitulo() + " - " + prioridadeLabel + " - " + data);
            }
            return c;
        }
    }
}
