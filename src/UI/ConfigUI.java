/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entities.Usuario;
import Enum.Role;
import Persistence.Configs.*;
import Persistence.Configs.Worker2Persistence.Worker2Config;
import Persistence.JsonPersistence;
import static Persistence.JsonPersistence.alterarUsuarioNoJson;
import static Persistence.JsonPersistence.removerUsuarioDoJson;
import static Persistence.JsonPersistence.salvarJsonEmAppData;
import Utils.ManipularImagem;
import Utils.RoundedBorder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mauros
 */
public class ConfigUI extends javax.swing.JFrame {

    private ArrayList<Usuario> UserArray = new ArrayList<>();
    private String ntaImgDir;

    /**
     * Creates new form ConfigUI
     */
    public ConfigUI() {
        initComponents();
        initImg();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        carregarInformacoes();
        carregarInformacoesArquivo();
        exibirInformacoesArray();
        carregarComponentesCBX();
        carregarInfoUsuarioAlt();
    }

    public void carregarComponentesCBX() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        for (Usuario user : UserArray) {
            model.addElement(user.getUsuario());
        }
        usuarioRemCBX.setModel(model);
        usuarioAltCBX.setModel(model);
    }

    public void carregarInfoUsuarioAlt() {
        for (Usuario users : UserArray) {
            if (users.getUsuario().equals(usuarioRemCBX.getSelectedItem().toString())) {
                nomeTF.setText(users.getNomeCompleto());
                mailAltTF.setText(users.getEmail());
                mailAltTF.setText(users.getEmail());
                senhaAltPWF.setText(new String(users.getSenha()));
                RoleAltCBX.setModel(new DefaultComboBoxModel<>(Role.values()));
            }
        }
    }

    public void addUser() {
        String nomeArquivo = "Users.json";
        String workspace = "UserConfig";

        UsuarioPersistence.SessionValues novoUsuario = new UsuarioPersistence.SessionValues();
        novoUsuario.usuario = usuarioAddTF.getText();
        novoUsuario.nomeCompleto = nomeAddTF.getText();
        novoUsuario.senha = new String(senhaPWF.getPassword());
        novoUsuario.email = mailAddTF.getText();
        novoUsuario.role = Role.valueOf(roleAddCBX.getSelectedItem().toString());

        if (ntaImgDir != null) {
            novoUsuario.imageDir = ntaImgDir;
        } else {
            novoUsuario.imageDir = "Sem imagem";
        }
        ntaImgDir = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        novoUsuario.acesso = LocalDateTime.now().format(formatter);

        List<UsuarioPersistence> listaUsuarios = JsonPersistence.carregarJsonAppdataUsuario(nomeArquivo);
        if (listaUsuarios == null) {
            listaUsuarios = new ArrayList<>();
        }

        for (UsuarioPersistence up : listaUsuarios) {
            for (UsuarioPersistence.SessionValues user : up.session) {
                if (user.usuario.equalsIgnoreCase(novoUsuario.usuario)) {
                    JOptionPane.showMessageDialog(null, "Usuário já existe: " + user.usuario, "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (user.email.equalsIgnoreCase(novoUsuario.email)) {
                    JOptionPane.showMessageDialog(null, "Já existe um usuário com esse e-mail: " + user.email, "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        JsonPersistence.adicionarUsuarioAoJson(nomeArquivo, workspace, novoUsuario);
        JOptionPane.showMessageDialog(null, "Usuário adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void persistirInformacoes() {
        Worker2Config config = new Worker2Config();
        config.workspace = "1";
        config.session = new Worker2Config.SessionValues();
        config.session.servidor = hostTF.getText();
        config.session.porta = portaTF.getText();
        config.session.senha = new String(senhaPWF.getPassword());
        config.session.protocolo = protocoloTF.getSelectedIndex();
        config.session.remetente = remetenteTF.getText();
        config.session.destinatario = destinatarioTF.getText();
        config.session.tituloMail = tituloTF.getText();
        config.session.corpoMail = corpoTF.getText();
        config.session.tls = tlsCBX.isSelected();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        salvarJsonEmAppData("Worker2Config" + ".json", gson.toJson(config), "/Persistence/Worker2");
    }

    public void carregarInformacoes() {
        String nomeArquivo = "Worker2Config" + ".json";
        Worker2Persistence.Worker2Config config = JsonPersistence.carregarJsonAppdata(nomeArquivo, Worker2Persistence.Worker2Config.class, "/Persistence/Worker2");

        if (config == null || config.session
                == null) {
            System.out.println("Arquivo de configuração não encontrado ou inválido: " + nomeArquivo);
            return;
        }

        try {
            portaTF.setText(config.session.porta);
            hostTF.setText(config.session.servidor);

            senhaPWF.setText(new String(config.session.senha));
            protocoloTF.setSelectedIndex(config.session.protocolo);
            remetenteTF.setText(config.session.remetente);
            destinatarioTF.setText(config.session.destinatario);
            tituloTF.setText(config.session.tituloMail);
            corpoTF.setText(config.session.corpoMail);
            tlsCBX.setSelected(config.session.tls);

        } catch (Exception e) {
            System.err.println("Erro ao carregar valores do JSON para os componentes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        Listar = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        listarUsuarioL = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        homeL4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Adicionar = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel12 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        criarUsuarioL = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        homeL = new javax.swing.JLabel();
        editarAddTB = new javax.swing.JToggleButton();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        LayoutCentral = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        usuarioAddTF = new javax.swing.JTextField();
        nomeAddTF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        senhaAddPWF = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        mailAddTF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        roleAddCBX = new javax.swing.JComboBox();
        userL = new javax.swing.JLabel();
        localImgL = new javax.swing.JLabel();
        alterarB = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Remover = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        removerUsuarioL = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        homeL1 = new javax.swing.JLabel();
        editarRemTB = new javax.swing.JToggleButton();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        LayoutCentral1 = new javax.swing.JPanel();
        userL1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        usuarioRemCBX = new javax.swing.JComboBox();
        Alterar = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel13 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        alterarUsuarioL = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        homeL3 = new javax.swing.JLabel();
        editarAltTB = new javax.swing.JToggleButton();
        jPanel28 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        LayoutCentral3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        nomeTF = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        senhaAltPWF = new javax.swing.JPasswordField();
        jLabel16 = new javax.swing.JLabel();
        mailAltTF = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        RoleAltCBX = new javax.swing.JComboBox();
        userL3 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        usuarioAltCBX = new javax.swing.JComboBox();
        localImgAltL = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        Listar1 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        configEmailL = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        salvarB = new javax.swing.JButton();
        homeL2 = new javax.swing.JLabel();
        editarTGB = new javax.swing.JToggleButton();
        painelCentral = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        hostTF = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        portaTF = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        senhaPWF = new javax.swing.JPasswordField();
        destinatarioTF = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        remetenteTF = new javax.swing.JTextField();
        tituloTF = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        corpoTF = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        tlsCBX = new javax.swing.JCheckBox();
        protocoloTF = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Ajustes");

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1045, 850));

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        Listar.setLayout(new java.awt.BorderLayout());

        jPanel4.setPreferredSize(new java.awt.Dimension(50, 594));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 614, Short.MAX_VALUE)
        );

        Listar.add(jPanel4, java.awt.BorderLayout.LINE_START);

        jPanel5.setPreferredSize(new java.awt.Dimension(1038, 90));

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel15.setText("Grid de Usuários");

        listarUsuarioL.setText(".");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(listarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(622, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        Listar.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel6.setPreferredSize(new java.awt.Dimension(50, 594));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 614, Short.MAX_VALUE)
        );

        Listar.add(jPanel6, java.awt.BorderLayout.LINE_END);

        jPanel7.setPreferredSize(new java.awt.Dimension(1038, 90));

        homeL4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeL4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeL4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeL4MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(514, Short.MAX_VALUE)
                .addComponent(homeL4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(461, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeL4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        Listar.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Usuário", "Nome", "Email", "Role", "Último Acesso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        Listar.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("Listar", Listar);

        Adicionar.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Adicionar.add(jPanel8, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Adicionar.add(jPanel9, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Adicionar.add(jPanel10, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Adicionar.add(jPanel11, java.awt.BorderLayout.LINE_END);

        jPanel12.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel1.setText("Criar Usuário");

        criarUsuarioL.setText(".");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(criarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(648, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(criarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel14, java.awt.BorderLayout.PAGE_START);

        jButton3.setText("Salvar");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        homeL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeLMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeLMouseExited(evt);
            }
        });

        editarAddTB.setText("Editar");
        editarAddTB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editarAddTBStateChanged(evt);
            }
        });
        editarAddTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarAddTBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(326, Short.MAX_VALUE)
                .addComponent(homeL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editarAddTB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(324, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(homeL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editarAddTB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel16, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel17, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );

        jPanel12.add(jPanel18, java.awt.BorderLayout.LINE_END);

        jLabel2.setText("Usuário");

        usuarioAddTF.setEnabled(false);

        nomeAddTF.setEnabled(false);

        jLabel3.setText("Nome completo");

        jLabel4.setText("Senha (Inicial)");

        senhaAddPWF.setEnabled(false);

        jLabel5.setText("Endereço de Email");

        mailAddTF.setEnabled(false);

        jLabel6.setText("Role");

        roleAddCBX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Convidado", "Administrador", "Super" }));
        roleAddCBX.setEnabled(false);

        userL.setText(".");

        localImgL.setText("Sem imagem");
        localImgL.setToolTipText("");

        alterarB.setText("Alterar");
        alterarB.setEnabled(false);
        alterarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alterarBActionPerformed(evt);
            }
        });

        jButton2.setText("Remover");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LayoutCentralLayout = new javax.swing.GroupLayout(LayoutCentral);
        LayoutCentral.setLayout(LayoutCentralLayout);
        LayoutCentralLayout.setHorizontalGroup(
            LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LayoutCentralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LayoutCentralLayout.createSequentialGroup()
                        .addComponent(alterarB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2))
                    .addComponent(userL, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(LayoutCentralLayout.createSequentialGroup()
                .addContainerGap(66, Short.MAX_VALUE)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(LayoutCentralLayout.createSequentialGroup()
                        .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(senhaAddPWF, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(usuarioAddTF, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(nomeAddTF, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mailAddTF, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(roleAddCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(localImgL)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LayoutCentralLayout.setVerticalGroup(
            LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userL, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(localImgL)
                .addGap(13, 13, 13)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alterarB)
                    .addComponent(jButton2))
                .addGap(54, 54, 54)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usuarioAddTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nomeAddTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LayoutCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(senhaAddPWF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mailAddTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roleAddCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(111, 111, 111))
        );

        jPanel12.add(LayoutCentral, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel12);

        Adicionar.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("Adicionar", Adicionar);

        Remover.setLayout(new java.awt.BorderLayout());

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel8.setText("Remover Usuário");

        removerUsuarioL.setText(".");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(removerUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(606, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removerUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        Remover.add(jPanel15, java.awt.BorderLayout.PAGE_START);

        jButton5.setText("Remover");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        homeL1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeL1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeL1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeL1MouseExited(evt);
            }
        });

        editarRemTB.setText("Editar");
        editarRemTB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editarRemTBStateChanged(evt);
            }
        });
        editarRemTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarRemTBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(322, Short.MAX_VALUE)
                .addComponent(homeL1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(editarRemTB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(317, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(homeL1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editarRemTB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        Remover.add(jPanel19, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        Remover.add(jPanel20, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        Remover.add(jPanel21, java.awt.BorderLayout.LINE_END);

        userL1.setText(".");

        jLabel14.setText("Usuário");

        usuarioRemCBX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "." }));
        usuarioRemCBX.setEnabled(false);
        usuarioRemCBX.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                usuarioRemCBXItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout LayoutCentral1Layout = new javax.swing.GroupLayout(LayoutCentral1);
        LayoutCentral1.setLayout(LayoutCentral1Layout);
        LayoutCentral1Layout.setHorizontalGroup(
            LayoutCentral1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LayoutCentral1Layout.createSequentialGroup()
                .addContainerGap(236, Short.MAX_VALUE)
                .addGroup(LayoutCentral1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(usuarioRemCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 226, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentral1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userL1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LayoutCentral1Layout.setVerticalGroup(
            LayoutCentral1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentral1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userL1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usuarioRemCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(409, 409, 409))
        );

        Remover.add(LayoutCentral1, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("Remover", Remover);

        Alterar.setLayout(new java.awt.BorderLayout());

        jPanel13.setLayout(new java.awt.BorderLayout());

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel10.setText("Alterar Usuário");

        alterarUsuarioL.setText(".");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(alterarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(625, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alterarUsuarioL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel26, java.awt.BorderLayout.PAGE_START);

        jButton9.setText("Salvar");
        jButton9.setToolTipText("");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        homeL3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeL3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeL3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeL3MouseExited(evt);
            }
        });

        editarAltTB.setText("Editar");
        editarAltTB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editarAltTBStateChanged(evt);
            }
        });
        editarAltTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarAltTBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(326, Short.MAX_VALUE)
                .addComponent(homeL3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editarAltTB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(324, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(homeL3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editarAltTB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel27, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel28, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel29, java.awt.BorderLayout.LINE_END);

        jLabel11.setText("Usuário");

        nomeTF.setEnabled(false);

        jLabel12.setText("Nome completo");

        jLabel13.setText("Senha (Inicial)");

        senhaAltPWF.setEnabled(false);

        jLabel16.setText("Endereço de Email");

        mailAltTF.setEnabled(false);

        jLabel17.setText("Role");

        RoleAltCBX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Convidado", "Adminstrador", "SUPER" }));
        RoleAltCBX.setEnabled(false);

        userL3.setText(".");

        jButton11.setText("Alterar");
        jButton11.setEnabled(false);
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton11MouseClicked(evt);
            }
        });
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Remover");
        jButton12.setEnabled(false);

        usuarioAltCBX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "." }));
        usuarioAltCBX.setEnabled(false);
        usuarioAltCBX.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                usuarioAltCBXItemStateChanged(evt);
            }
        });

        localImgAltL.setText("Sem imagem");

        javax.swing.GroupLayout LayoutCentral3Layout = new javax.swing.GroupLayout(LayoutCentral3);
        LayoutCentral3.setLayout(LayoutCentral3Layout);
        LayoutCentral3Layout.setHorizontalGroup(
            LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LayoutCentral3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LayoutCentral3Layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton12))
                    .addComponent(userL3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(LayoutCentral3Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(LayoutCentral3Layout.createSequentialGroup()
                        .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(senhaAltPWF, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13)
                            .addComponent(usuarioAltCBX, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel12)
                            .addComponent(nomeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mailAltTF, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(RoleAltCBX, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentral3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(localImgAltL)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LayoutCentral3Layout.setVerticalGroup(
            LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LayoutCentral3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userL3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(localImgAltL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(jButton12))
                .addGap(54, 54, 54)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nomeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usuarioAltCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LayoutCentral3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(senhaAltPWF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mailAltTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RoleAltCBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(111, 111, 111))
        );

        jPanel13.add(LayoutCentral3, java.awt.BorderLayout.CENTER);

        jScrollPane4.setViewportView(jPanel13);

        Alterar.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jTabbedPane2.addTab("Alterar", Alterar);

        jPanel1.add(jTabbedPane2);

        jTabbedPane1.addTab("Usuários", jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        Listar1.setLayout(new java.awt.BorderLayout());

        jPanel22.setPreferredSize(new java.awt.Dimension(50, 594));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        Listar1.add(jPanel22, java.awt.BorderLayout.LINE_START);

        jPanel23.setPreferredSize(new java.awt.Dimension(1038, 90));

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        jLabel18.setText("Configurar Notificação");

        configEmailL.setText(".");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(configEmailL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(546, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(configEmailL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        Listar1.add(jPanel23, java.awt.BorderLayout.PAGE_START);

        jPanel24.setPreferredSize(new java.awt.Dimension(50, 594));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        Listar1.add(jPanel24, java.awt.BorderLayout.LINE_END);

        jPanel25.setPreferredSize(new java.awt.Dimension(1038, 116));

        salvarB.setText("Salvar");
        salvarB.setEnabled(false);

        homeL2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeL2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeL2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeL2MouseExited(evt);
            }
        });

        editarTGB.setText("Editar");
        editarTGB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editarTGBStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(324, Short.MAX_VALUE)
                .addComponent(homeL2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(salvarB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(editarTGB, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(317, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(editarTGB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(salvarB, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(homeL2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        Listar1.add(jPanel25, java.awt.BorderLayout.PAGE_END);

        jLabel9.setText("Servidor Remoto");

        hostTF.setText(".");
        hostTF.setEnabled(false);
        hostTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                hostTFKeyReleased(evt);
            }
        });

        jLabel19.setText("Porta");

        portaTF.setEnabled(false);
        portaTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                portaTFKeyReleased(evt);
            }
        });

        jLabel20.setText("Senha");

        senhaPWF.setEnabled(false);
        senhaPWF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                senhaPWFKeyReleased(evt);
            }
        });

        destinatarioTF.setEnabled(false);
        destinatarioTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                destinatarioTFKeyReleased(evt);
            }
        });

        jLabel21.setText("Remetente");

        jLabel22.setText("Destinatário");

        remetenteTF.setEnabled(false);
        remetenteTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                remetenteTFKeyReleased(evt);
            }
        });

        tituloTF.setEnabled(false);
        tituloTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tituloTFMouseEntered(evt);
            }
        });

        jLabel23.setText("Título");

        corpoTF.setEnabled(false);
        corpoTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                corpoTFKeyReleased(evt);
            }
        });

        jLabel24.setText("Corpo do E-mail");

        tlsCBX.setText("STARTTLS");
        tlsCBX.setEnabled(false);
        tlsCBX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tlsCBXStateChanged(evt);
            }
        });

        protocoloTF.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TLSv1.2", "SSLv3.0" }));
        protocoloTF.setEnabled(false);
        protocoloTF.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                protocoloTFItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout painelCentralLayout = new javax.swing.GroupLayout(painelCentral);
        painelCentral.setLayout(painelCentralLayout);
        painelCentralLayout.setHorizontalGroup(
            painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCentralLayout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tituloTF)
                        .addComponent(senhaPWF, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(hostTF, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(remetenteTF, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24)
                    .addComponent(corpoTF)
                    .addComponent(jLabel19)
                    .addComponent(portaTF, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(destinatarioTF)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelCentralLayout.createSequentialGroup()
                        .addComponent(protocoloTF, 0, 251, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(tlsCBX)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        painelCentralLayout.setVerticalGroup(
            painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCentralLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portaTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(painelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(10, 10, 10)
                        .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(senhaPWF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tlsCBX)
                            .addComponent(protocoloTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(remetenteTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(painelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(destinatarioTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(painelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tituloTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(painelCentralLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(corpoTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(318, Short.MAX_VALUE))
        );

        Listar1.add(painelCentral, java.awt.BorderLayout.CENTER);

        jTabbedPane3.addTab("Worker 2", Listar1);

        jPanel2.add(jTabbedPane3);

        jTabbedPane1.addTab("Notificações", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homeL2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL2MouseExited
        homeL2.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeL2MouseExited

    private void homeL2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL2MouseEntered
        homeL2.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeL2MouseEntered

    private void homeL2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL2MouseClicked
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeL2MouseClicked

    private void homeL3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL3MouseExited
        homeL3.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeL3MouseExited

    private void homeL3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL3MouseEntered
        homeL3.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeL3MouseEntered

    private void homeL3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL3MouseClicked
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeL3MouseClicked

    private void homeL1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL1MouseExited
        homeL1.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeL1MouseExited

    private void homeL1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL1MouseEntered
        homeL1.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeL1MouseEntered

    private void homeL1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL1MouseClicked
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeL1MouseClicked

    private void homeLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseExited
        homeL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeLMouseExited

    private void homeLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseEntered
        homeL.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeLMouseEntered

    private void homeLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseClicked
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeLMouseClicked

    private void homeL4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL4MouseExited
        homeL4.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeL4MouseExited

    private void homeL4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL4MouseEntered
        homeL4.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeL4MouseEntered

    private void homeL4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeL4MouseClicked
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeL4MouseClicked

    private void hostTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hostTFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_hostTFKeyReleased

    private void portaTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_portaTFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_portaTFKeyReleased

    private void senhaPWFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_senhaPWFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_senhaPWFKeyReleased

    private void protocoloTFItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_protocoloTFItemStateChanged
        persistirInformacoes();
    }//GEN-LAST:event_protocoloTFItemStateChanged

    private void tlsCBXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tlsCBXStateChanged
        persistirInformacoes();
    }//GEN-LAST:event_tlsCBXStateChanged

    private void remetenteTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_remetenteTFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_remetenteTFKeyReleased

    private void destinatarioTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_destinatarioTFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_destinatarioTFKeyReleased

    private void tituloTFMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tituloTFMouseEntered
        persistirInformacoes();
    }//GEN-LAST:event_tituloTFMouseEntered

    private void corpoTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_corpoTFKeyReleased
        persistirInformacoes();
    }//GEN-LAST:event_corpoTFKeyReleased

    private void editarTGBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_editarTGBStateChanged
        if (editarTGB.isSelected()) {
            hostTF.setEnabled(true);
            portaTF.setEnabled(true);
            senhaPWF.setEnabled(true);
            protocoloTF.setEnabled(true);
            tlsCBX.setEnabled(true);
            remetenteTF.setEnabled(true);
            destinatarioTF.setEnabled(true);
            tituloTF.setEnabled(true);
            corpoTF.setEnabled(true);
            salvarB.setEnabled(true);
            return;
        }
        hostTF.setEnabled(false);
        portaTF.setEnabled(false);
        senhaPWF.setEnabled(false);
        protocoloTF.setEnabled(false);
        tlsCBX.setEnabled(false);
        remetenteTF.setEnabled(false);
        destinatarioTF.setEnabled(false);
        tituloTF.setEnabled(false);
        corpoTF.setEnabled(false);
        salvarB.setEnabled(false);
    }//GEN-LAST:event_editarTGBStateChanged

    private void usuarioAltCBXItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_usuarioAltCBXItemStateChanged
        carregarInfoUsuarioAlt();
        Usuario user = this.pesquisarUsuarioPorNome(usuarioAltCBX.getSelectedItem().toString());
        alterarIconAddUser(user.getImageDir(), userL3);
        localImgAltL.setText(user.getImageDir());
    }//GEN-LAST:event_usuarioAltCBXItemStateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!"Sem imagem".equals(localImgL.getText())) {
            ntaImgDir = ManipularImagem.copiarImagemParaAppData(localImgL.getText());
        }

        if (usuarioAddTF.getText().length() > 0 && nomeAddTF.getText().length() > 0 && senhaAddPWF.getPassword().length > 0 && new String(senhaAddPWF.getPassword()).length() > 0) {
            this.addUser();
            this.UserArray.clear();
            this.carregarInformacoesArquivo();
            this.carregarInformacoes();
            this.exibirInformacoesArray();
            carregarComponentesCBX();
            return;
        }
        JOptionPane.showMessageDialog(null, "Campos obrigatórios não preenchidos!", "Aviso", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JFileChooser fileChooser;

        String userHome = System.getProperty("user.home");
        String imagensPath = userHome + File.separator + "Pictures";
        File imagensDir = new File(imagensPath);

        if (imagensDir.exists() && imagensDir.isDirectory()) {
            fileChooser = new JFileChooser(imagensDir);
        } else {
            fileChooser = new JFileChooser();
        }

        fileChooser.setDialogTitle("Selecionar imagem");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Adiciona filtro para aceitar somente arquivos de imagem
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Imagens (*.jpg, *.jpeg, *.png, *.bmp, *.gif)", "jpg", "jpeg", "png", "bmp", "gif"
        );
        fileChooser.setFileFilter(imageFilter);

        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            String caminhoCompleto = arquivoSelecionado.getAbsolutePath();

            System.out.println("Imagem selecionada: " + caminhoCompleto);
            localImgAltL.setText(caminhoCompleto);
            alterarIconAddUser(caminhoCompleto, userL3);
        } else {
            System.out.println("Nenhuma imagem selecionada.");
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseClicked

    }//GEN-LAST:event_jButton11MouseClicked

    private void alterarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alterarBActionPerformed
        JFileChooser fileChooser;

        String userHome = System.getProperty("user.home");
        String imagensPath = userHome + File.separator + "Pictures";
        File imagensDir = new File(imagensPath);

        if (imagensDir.exists() && imagensDir.isDirectory()) {
            fileChooser = new JFileChooser(imagensDir);
        } else {
            fileChooser = new JFileChooser();
        }

        fileChooser.setDialogTitle("Selecionar imagem");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Adiciona filtro para aceitar somente arquivos de imagem
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Imagens (*.jpg, *.jpeg, *.png, *.bmp, *.gif)", "jpg", "jpeg", "png", "bmp", "gif"
        );
        fileChooser.setFileFilter(imageFilter);

        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            String caminhoCompleto = arquivoSelecionado.getAbsolutePath();

            System.out.println("Imagem selecionada: " + caminhoCompleto);
            localImgL.setText(caminhoCompleto);
            alterarIconAddUser(caminhoCompleto, userL);
        } else {
            System.out.println("Nenhuma imagem selecionada.");
        }
    }//GEN-LAST:event_alterarBActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        localImgL.setText("Sem imagem");
        alterarIconAddUser(getClass().getClassLoader().getResource("imgs/userIcon.png").getPath(), userL);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void editarAddTBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_editarAddTBStateChanged

    }//GEN-LAST:event_editarAddTBStateChanged

    private void editarAddTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarAddTBActionPerformed

        if (editarAddTB.isSelected()) {
            alterarB.setEnabled(true);
            jButton2.setEnabled(true);
            usuarioAddTF.setEnabled(true);
            nomeAddTF.setEnabled(true);
            senhaAddPWF.setEnabled(true);
            mailAddTF.setEnabled(true);
            roleAddCBX.setEnabled(true);
            jButton3.setEnabled(true);
        } else {
            alterarB.setEnabled(false);
            jButton2.setEnabled(false);
            usuarioAddTF.setEnabled(false);
            nomeAddTF.setEnabled(false);
            senhaAddPWF.setEnabled(false);
            mailAddTF.setEnabled(false);
            roleAddCBX.setEnabled(false);
            jButton3.setEnabled(false);
        }
    }//GEN-LAST:event_editarAddTBActionPerformed

    private void editarRemTBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_editarRemTBStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_editarRemTBStateChanged

    private void editarRemTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarRemTBActionPerformed
        // TODO add your handling code here:
        if (editarRemTB.isSelected()) {
            usuarioRemCBX.setEnabled(true);
            jButton5.setEnabled(true);
            alterarIconAddUser(this.pesquisarUsuarioPorNome(usuarioRemCBX.getSelectedItem().toString()).getImageDir(), userL1);

        } else {
            usuarioRemCBX.setEnabled(false);
            jButton5.setEnabled(false);
        }
    }//GEN-LAST:event_editarRemTBActionPerformed

    private void editarAltTBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_editarAltTBStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_editarAltTBStateChanged

    private void editarAltTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarAltTBActionPerformed
        alterarIconAddUser(this.pesquisarUsuarioPorNome(usuarioAltCBX.getSelectedItem().toString()).getImageDir(), userL3);
        if (editarAltTB.isSelected()) {
            usuarioAltCBX.setEnabled(true);
            nomeTF.setEnabled(true);
            senhaAltPWF.setEnabled(true);
            mailAltTF.setEnabled(true);
            RoleAltCBX.setEnabled(true);
            jButton11.setEnabled(true);
            jButton12.setEnabled(true);
            jButton9.setEnabled(true);
            Usuario user = this.pesquisarUsuarioPorNome(usuarioAltCBX.getSelectedItem().toString());
            alterarIconAddUser(user.getImageDir(), userL3);
        } else {
            usuarioAltCBX.setEnabled(false);
            nomeTF.setEnabled(false);
            senhaAltPWF.setEnabled(false);
            mailAltTF.setEnabled(false);
            RoleAltCBX.setEnabled(false);
            jButton11.setEnabled(false);
            jButton12.setEnabled(false);
            jButton9.setEnabled(false);

        }
    }//GEN-LAST:event_editarAltTBActionPerformed

    private void usuarioRemCBXItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_usuarioRemCBXItemStateChanged
        alterarIconAddUser(this.pesquisarUsuarioPorNome(usuarioRemCBX.getSelectedItem().toString()).getImageDir(), userL1);
    }//GEN-LAST:event_usuarioRemCBXItemStateChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        removerUsuarioDoJson("Users.json", usuarioRemCBX.getSelectedItem().toString());
        this.UserArray.clear();
        this.carregarInformacoesArquivo();
        this.carregarInformacoes();
        this.exibirInformacoesArray();
        carregarComponentesCBX();
        alterarIconAddUser(this.pesquisarUsuarioPorNome(usuarioRemCBX.getSelectedItem().toString()).getImageDir(), userL1);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        if (!"Sem imagem".equals(localImgAltL.getText())) {
            ntaImgDir = ManipularImagem.copiarImagemParaAppData(localImgAltL.getText());
        }

        if (usuarioAltCBX.getSelectedItem().toString().length() > 0 && nomeTF.getText().length() > 0 && senhaAltPWF.getPassword().length > 0 && mailAltTF.getText().length() > 0) {
            this.alterUser();
            this.UserArray.clear();
            this.carregarInformacoesArquivo();
            this.carregarInformacoes();
            this.exibirInformacoesArray();
            carregarComponentesCBX();
            return;
        }
        JOptionPane.showMessageDialog(null, "Campos obrigatórios não preenchidos!", "Aviso", JOptionPane.WARNING_MESSAGE);

        this.UserArray.clear();
        this.carregarInformacoesArquivo();
        this.carregarInformacoes();
        this.exibirInformacoesArray();
        carregarComponentesCBX();

        Usuario user = this.pesquisarUsuarioPorNome(usuarioAltCBX.getSelectedItem().toString());
        alterarIconAddUser(user.getImageDir(), userL3);
        localImgAltL.setText(user.getImageDir());
    }//GEN-LAST:event_jButton9ActionPerformed

    private void alterUser() {
        UsuarioPersistence.SessionValues novoUsuario = new UsuarioPersistence.SessionValues();
        novoUsuario.usuario = usuarioAltCBX.getSelectedItem().toString();
        novoUsuario.nomeCompleto = nomeTF.getText();
        novoUsuario.senha = new String(senhaAltPWF.getPassword());
        novoUsuario.email = mailAltTF.getText();
        novoUsuario.role = Role.valueOf(RoleAltCBX.getSelectedItem().toString());

        if (ntaImgDir != null) {
            novoUsuario.imageDir = ntaImgDir;
        } else {
            novoUsuario.imageDir = "Sem imagem";
        }
        ntaImgDir = null;

        alterarUsuarioNoJson("Users.json", usuarioAltCBX.getSelectedItem().toString(), novoUsuario);
    }

    private void alterarIconAddUser(String local, javax.swing.JLabel lbl) {

        if ("Sem imagem".equals(local)) {
            local = getClass().getClassLoader().getResource("imgs/userIcon.png").getPath();
        }

        Image alterarUsuarioLogo = this.getScaledImage(local, lbl);
        setScaledImage(lbl, alterarUsuarioLogo);
    }

    private Usuario pesquisarUsuarioPorNome(String input) {
        for (Usuario user : UserArray) {
            if (user.getUsuario().toLowerCase().equals(input.toLowerCase())) {
                return user;
            }
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConfigUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConfigUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConfigUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConfigUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigUI().setVisible(true);
            }
        });
    }

    private void initImg() {
        //ScaledImages
        Image userLogo = this.getScaledImage("imgs/userIcon.png", userL, true);
        Image userLogo1 = this.getScaledImage("imgs/userIcon.png", userL1, true);
        Image userCreateLogo = this.getScaledImage("imgs/userAddIcon.png", criarUsuarioL, true);
        Image userRemoveLogo = this.getScaledImage("imgs/userRemoveIcon.png", removerUsuarioL, true);
        Image alterarUsuarioLogo = this.getScaledImage("imgs/userEditIcon.png", alterarUsuarioL, true);
        Image listarUsuarioLogo = this.getScaledImage("imgs/userListIcon.png", listarUsuarioL, true);

        Image configEmailLogo = this.getScaledImage("imgs/notificationWorker.png", configEmailL, true);

        javax.swing.JLabel homeLN = homeL;
        javax.swing.JLabel homeLN1 = homeL1;
        javax.swing.JLabel homeLN3 = homeL3;
        javax.swing.JLabel homeLN4 = homeL4;
        javax.swing.JLabel homeLN2 = homeL2;
        homeLN.setBounds(homeLN.getX(), homeLN.getY(), homeLN.getWidth() - 20, homeLN.getHeight() - 20);
        Image homeLogo = this.getScaledImage("imgs/home_Icon.png", homeLN, true);

        //Setter Images
        setScaledImage(userL, userLogo);
        setScaledImage(criarUsuarioL, userCreateLogo);
        setScaledImage(homeLN, homeLogo);
        setScaledImage(homeLN1, homeLogo);
        setScaledImage(homeLN3, homeLogo);
        setScaledImage(homeLN4, homeLogo);
        setScaledImage(homeLN2, homeLogo);

        setScaledImage(removerUsuarioL, userRemoveLogo);
        setScaledImage(userL1, userLogo1);
        setScaledImage(alterarUsuarioL, alterarUsuarioLogo);
        setScaledImage(userL3, userLogo1);
        setScaledImage(listarUsuarioL, listarUsuarioLogo);

        setScaledImage(configEmailL, configEmailLogo);

        homeL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        homeL1.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        homeL3.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        homeL4.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        homeL2.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));

        Font fonteTabela = new Font("Segoe UI", Font.PLAIN, 13);
        jTable1.setFont(fonteTabela);

        jTable1.setShowGrid(true);
        jTable1.setGridColor(Color.LIGHT_GRAY);
        jTable1.setRowHeight(25);  // altura confortável para leitura
        jTable1.setIntercellSpacing(new Dimension(1, 1));  // define espaçamento entre células

        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);

        jTable1.getColumnModel().getColumn(0).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(1).setCellRenderer(criarRendererComZebra(SwingConstants.LEFT));
        jTable1.getColumnModel().getColumn(2).setCellRenderer(criarRendererComZebra(SwingConstants.LEFT));
        jTable1.getColumnModel().getColumn(3).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));
        jTable1.getColumnModel().getColumn(4).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));
    }

    private Image getScaledImage(String directory, javax.swing.JLabel label, boolean scaled) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(directory));
        Image image = icon.getImage();

        if (scaled) {
            Image ScaledImage = image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            return ScaledImage;
        } else {
            return image;
        }
    }

    private Image getScaledImage(String directory, javax.swing.JLabel label) {
        ImageIcon icon = new ImageIcon(directory);
        Image image = icon.getImage();

        Image ScaledImage = image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        return ScaledImage;
    }

    private void setScaledImage(javax.swing.JLabel label, Image image) {
        label.setIcon(new javax.swing.ImageIcon(image));
    }

    private DefaultTableCellRenderer criarRendererComZebra(int alinhamento) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(alinhamento);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        };
    }

    public void carregarInformacoesArquivo() {
        String nomeArquivo = "Users.json";
        List<UsuarioPersistence> listaUsuarios = JsonPersistence.carregarJsonAppdataUsuario(nomeArquivo);
        if (listaUsuarios == null || listaUsuarios.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado ou inválido: " + nomeArquivo);
            return;
        }
        for (UsuarioPersistence config : listaUsuarios) {
            for (UsuarioPersistence.SessionValues entry : config.session) {
                this.addToArray(entry.imageDir, entry.usuario, entry.nomeCompleto, entry.senha, entry.email, entry.role, entry.acesso);
            }
        }

    }

    private void addToArray(String imageDir, String user, String nomeCompleto, String senha, String email, Role role, String acesso) {
        Usuario usuario = new Usuario(imageDir, user, nomeCompleto, senha, email, role, acesso);
        this.UserArray.add(usuario);
    }

    private void exibirInformacoesArray() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //22/03/1999 01:00:00

        for (Usuario user : UserArray) {
            model.addRow(new Object[]{
                user.getUsuario(),
                user.getNomeCompleto(),
                user.getEmail(),
                user.getRole(),
                user.getAcesso()
            });
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Adicionar;
    private javax.swing.JPanel Alterar;
    private javax.swing.JPanel LayoutCentral;
    private javax.swing.JPanel LayoutCentral1;
    private javax.swing.JPanel LayoutCentral3;
    private javax.swing.JPanel Listar;
    private javax.swing.JPanel Listar1;
    private javax.swing.JPanel Remover;
    private javax.swing.JComboBox RoleAltCBX;
    private javax.swing.JButton alterarB;
    private javax.swing.JLabel alterarUsuarioL;
    private javax.swing.JLabel configEmailL;
    private javax.swing.JTextField corpoTF;
    private javax.swing.JLabel criarUsuarioL;
    private javax.swing.JTextField destinatarioTF;
    private javax.swing.JToggleButton editarAddTB;
    private javax.swing.JToggleButton editarAltTB;
    private javax.swing.JToggleButton editarRemTB;
    private javax.swing.JToggleButton editarTGB;
    private javax.swing.JLabel homeL;
    private javax.swing.JLabel homeL1;
    private javax.swing.JLabel homeL2;
    private javax.swing.JLabel homeL3;
    private javax.swing.JLabel homeL4;
    private javax.swing.JTextField hostTF;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel listarUsuarioL;
    private javax.swing.JLabel localImgAltL;
    private javax.swing.JLabel localImgL;
    private javax.swing.JTextField mailAddTF;
    private javax.swing.JTextField mailAltTF;
    private javax.swing.JTextField nomeAddTF;
    private javax.swing.JTextField nomeTF;
    private javax.swing.JPanel painelCentral;
    private javax.swing.JTextField portaTF;
    private javax.swing.JComboBox protocoloTF;
    private javax.swing.JTextField remetenteTF;
    private javax.swing.JLabel removerUsuarioL;
    private javax.swing.JComboBox roleAddCBX;
    private javax.swing.JButton salvarB;
    private javax.swing.JPasswordField senhaAddPWF;
    private javax.swing.JPasswordField senhaAltPWF;
    private javax.swing.JPasswordField senhaPWF;
    private javax.swing.JTextField tituloTF;
    private javax.swing.JCheckBox tlsCBX;
    private javax.swing.JLabel userL;
    private javax.swing.JLabel userL1;
    private javax.swing.JLabel userL3;
    private javax.swing.JTextField usuarioAddTF;
    private javax.swing.JComboBox usuarioAltCBX;
    private javax.swing.JComboBox usuarioRemCBX;
    // End of variables declaration//GEN-END:variables
}
