/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Entities.*;
import Enum.*;
import Utils.*;
import Persistence.JsonPersistence;
import Persistence.Logs.LogPersistence;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Mauros
 */
public final class MonitoringUI extends javax.swing.JFrame {

    private boolean barraPesquisaPrimeiroAcesso = true;
    private boolean worker1 = false;
    private boolean worker2 = false;
    private ArrayList<LogOcurrenceMonitoring> LogArray = new ArrayList<>();

    /**
     * Creates new form MonitoringUI
     */
    public MonitoringUI() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initImg();
        inicializaFTF();
        SwingUtilities.invokeLater(() -> carregarInformacoesArquivo());
        SwingUtilities.invokeLater(() -> exibirInformacoesArray());
    }

    private void initImg() {
        javax.swing.JLabel worker1LN = worker1L;
        worker1LN.setBounds(worker1L.getX(), worker1L.getY(), worker1L.getWidth() - 60, worker1L.getHeight() - 20);
        javax.swing.JLabel worker2LN = worker2L;
        worker2LN.setBounds(worker2LN.getX(), worker2LN.getY(), worker2LN.getWidth() - 60, worker2LN.getHeight() - 20);
        javax.swing.JLabel homeLN = homeL;
        homeLN.setBounds(homeLN.getX(), homeLN.getY(), homeLN.getWidth() - 60, homeLN.getHeight() - 20);
        javax.swing.JLabel configLN = configL;
        configLN.setBounds(configLN.getX(), configLN.getY(), configLN.getWidth() - 60, configLN.getHeight() - 20);
        javax.swing.JLabel refreshLN = refreshL;
        refreshLN.setBounds(refreshLN.getX(), refreshLN.getY(), refreshLN.getWidth() - 60, refreshLN.getHeight() - 20);

        Image worker1Logo = this.getScaledImage("imgs/Worker.png", worker1LN, true);
        Image worker2Logo = this.getScaledImage("imgs/notificationWorker.png", worker2LN, true);
        Image homeLogo = this.getScaledImage("imgs/home_Icon.png", homeLN, true);
        Image configLogo = this.getScaledImage("imgs/sysadmin_icon.png", configLN, true);
        Image pesquisaLogo = this.getScaledImage("imgs/search_Icon.png", pesquisaL1, true);
        Image ntaLogo = this.getScaledImage("imgs/nta_logo2.png", ntaL, true);
        Image refreshLogo = this.getScaledImage("imgs/refresh_icon.png", refreshL, true);

        setScaledImage(worker1LN, worker1Logo);
        setScaledImage(worker2L, worker2Logo);
        setScaledImage(homeLN, homeLogo);
        setScaledImage(configLN, configLogo);
        setScaledImage(pesquisaL1, pesquisaLogo);
        setScaledImage(ntaL, ntaLogo);
        setScaledImage(refreshLN, refreshLogo);

        worker1L.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        worker2L.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        WorkersL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        menuL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        homeL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        configL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        statusL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        refreshL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
        

        campoPesquisaTF.setBorder(new RoundedBorder(Color.GRAY, 2, 30)); // Arredondado
        campoPesquisaTF.setOpaque(false); //
        campoPesquisaTF.setPreferredSize(new Dimension(300, 35)); // Tamanho desejado

        jTable1.setShowGrid(true);
        jTable1.setGridColor(Color.LIGHT_GRAY);
        jTable1.setFillsViewportHeight(true);
        jTable1.setRowHeight(25);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setBorder(BorderFactory.createLineBorder(Color.GRAY));

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

        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        jTable1.getColumnModel().getColumn(0).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));   // Data
        jTable1.getColumnModel().getColumn(1).setCellRenderer(criarRendererComZebra(SwingConstants.LEFT));     // Máquina
        jTable1.getColumnModel().getColumn(2).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));   // Level
        jTable1.getColumnModel().getColumn(3).setCellRenderer(criarRendererComZebra(SwingConstants.CENTER));   // Módulo
        jTable1.getColumnModel().getColumn(4).setCellRenderer(criarRendererComZebra(SwingConstants.LEFT));     // Log
        jTable1.getColumnModel().getColumn(5).setCellRenderer(criarRendererComZebra(SwingConstants.RIGHT));    // ICMP Echo Request

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

    private void setScaledImage(javax.swing.JLabel label, Image image) {
        label.setIcon(new javax.swing.ImageIcon(image));
    }

    public boolean isBarraPesquisaPrimeiroAcesso() {
        return barraPesquisaPrimeiroAcesso;
    }

    public void setBarraPesquisaPrimeiroAcesso(boolean barraPesquisaPrimeiroAcesso) {
        this.barraPesquisaPrimeiroAcesso = barraPesquisaPrimeiroAcesso;
    }

    public void carregarInformacoesArquivo() {
        String nomeArquivo = "LogNTA.json";
        List<LogPersistence> listaLogs = JsonPersistence.carregarJsonAppdataLog(nomeArquivo);
        if (listaLogs == null || listaLogs.isEmpty()) {
            System.out.println("Arquivo de configuração não encontrado ou inválido: " + nomeArquivo);
            return;
        }
        for (LogPersistence config : listaLogs) {
            for (LogPersistence.SessionValues entry : config.session) {
                this.addToArray(entry.maquina, entry.level, config.module, entry.log, entry.icmpRequest, entry.data);
            }
        }
        carregarBarraStatus();
    }

    private void carregarBarraStatus() {
        int qtdRegistros = 0;
        int qtdFine = 0;
        int qtdWarn = 0;
        int qtdError = 0;

        Set<String> hostsUnicos = new HashSet<>();

        for (LogOcurrenceMonitoring log : LogArray) {
            qtdRegistros++;

            hostsUnicos.add(log.getHost());

            if (log.getLevel() == LogLevel.DEBUG
                    || log.getLevel() == LogLevel.FINE
                    || log.getLevel() == LogLevel.INFO) {
                qtdFine++;
            }
            if (log.getLevel() == LogLevel.WARNING) {
                qtdWarn++;
            }
            if (log.getLevel() == LogLevel.ERROR || log.getLevel() == LogLevel.SEVERE) {
                qtdError++;
            }
        }

        registrosL.setText("Registros: " + qtdRegistros);
        bemL.setText("Bem: " + qtdFine);
        avisosL.setText("Avisos: " + qtdWarn);
        errosL.setText("Erros: " + qtdError);

        int qtdHostsDistintos = hostsUnicos.size();
        maquinasL.setText("Máquinas: " + qtdHostsDistintos);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataAtual = sdf.format(new Date());
        changeL.setText("Atualização: " + dataAtual);

        if (isWorker1()) {
            worker1LL.setText("Worker1: OK");
        } else {
            worker1LL.setText("Worker1: NOK");
        }
        if (isWorker2()) {
            worker2LL.setText("Worker2: OK");
        } else {
            worker2LL.setText("Worker2: NOK");
        }

    }

    //Metodo adiciona tudo oque recebe do arquivo de leitura ao ARRAY
    private void addToArray(String host, LogLevel level, String modulo, String inputLog, double icmp, String occurrence) {

        LogOcurrenceMonitoring log = new LogOcurrenceMonitoring(host, level, Module.valueOf(modulo), inputLog, icmp, occurrence);
        this.LogArray.add(log);
    }

    private void exibirInformacoesArray() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //22/03/1999 01:00:00
        if (dataCHB.isSelected()) {
            try {
                Date dataInicial = sdf.parse(dataInicialFTF.getText().trim());
                Date dataFinal = sdf.parse(dataFinalFTF.getText().trim());
                for (LogOcurrenceMonitoring log : LogArray) {
                    if (permitirLogGeracao(log.getLevel())) {
                        Date dataLog = sdf.parse(log.getOccurrence());
                        if (dataLog != null && !dataLog.before(dataInicial) && !dataLog.after(dataFinal)) {
                            model.addRow(new Object[]{
                                log.getOccurrence(),
                                log.getHost(),
                                log.getLevel().toString(),
                                log.getModulo().toString(),
                                log.getLog(),
                                log.getIcmp()
                            });
                        }
                    }
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha corretamente as datas nos campos ou verifique se o arquivo está com datas válidas!", "Data inválida", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            for (LogOcurrenceMonitoring log : LogArray) {
                if (permitirLogGeracao(log.getLevel())) {
                    model.addRow(new Object[]{
                        log.getOccurrence(),
                        log.getHost(),
                        log.getLevel().toString(),
                        log.getModulo().toString(),
                        log.getLog(),
                        log.getIcmp()
                    });
                }
            }
        }
    }

    public boolean permitirLogGeracao(LogLevel nivelGerado) {
        return nivelGerado.getPrioridade() >= (levelSL.getValue() + 1);
    }

    private void inicializaFTF() {
        // Máscara: dd/MM/yyyy HH:mm:ss
        MaskFormatter dateMask = null;

        try {
            dateMask = new MaskFormatter("##/##/#### ##:##:##");
        } catch (ParseException ex) {
            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        dateMask.setPlaceholderCharacter('_');

        dataInicialFTF.setColumns(20);
        dataInicialFTF.setFormatterFactory(new DefaultFormatterFactory(dateMask));
        dataFinalFTF.setColumns(20);
        dataFinalFTF.setFormatterFactory(new DefaultFormatterFactory(dateMask));
    }

    private void searchBarAction() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (LogOcurrenceMonitoring log : LogArray) {
            if (permitirLogGeracao(log.getLevel())) {
                if (log.getLog().toLowerCase().contains(campoPesquisaTF.getText().trim().toLowerCase())) {
                    model.addRow(new Object[]{
                        log.getOccurrence(),
                        log.getHost(),
                        log.getLevel().toString(),
                        log.getModulo().toString(),
                        log.getLog(),
                        log.getIcmp()
                    });
                }
            }
        }
    }

    public boolean isWorker1() {
        return worker1;
    }

    public void setWorker1(boolean worker1) {
        this.worker1 = worker1;
    }

    public void setWorker2(boolean worker2) {
        this.worker2 = worker2;
    }

    public boolean isWorker2() {
        return worker2;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        pesquisaL1 = new javax.swing.JLabel();
        campoPesquisaTF = new javax.swing.JTextField();
        levelSL = new javax.swing.JSlider();
        levelL = new javax.swing.JLabel();
        dataCHB = new javax.swing.JCheckBox();
        deL = new javax.swing.JLabel();
        dataInicialFTF = new javax.swing.JFormattedTextField();
        dataFinalFTF = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        filtrarB = new javax.swing.JButton();
        ntaL = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        worker2L = new javax.swing.JLabel();
        worker1L = new javax.swing.JLabel();
        WorkersL = new javax.swing.JLabel();
        homeL = new javax.swing.JLabel();
        menuL = new javax.swing.JLabel();
        configL = new javax.swing.JLabel();
        statusL = new javax.swing.JLabel();
        errosL = new javax.swing.JLabel();
        avisosL = new javax.swing.JLabel();
        bemL = new javax.swing.JLabel();
        registrosL = new javax.swing.JLabel();
        worker2LL = new javax.swing.JLabel();
        worker1LL = new javax.swing.JLabel();
        maquinasL = new javax.swing.JLabel();
        changeL = new javax.swing.JLabel();
        refreshL = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NTA - Painel de monitoramento");

        jTextPane1.setEditable(false);
        jScrollPane1.setViewportView(jTextPane1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setMaximumSize(new java.awt.Dimension(16383, 16383));
        jPanel3.setPreferredSize(new java.awt.Dimension(50, 487));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.LINE_START);

        jPanel4.setPreferredSize(new java.awt.Dimension(50, 487));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel4, java.awt.BorderLayout.LINE_END);

        pesquisaL1.setText(".");

        campoPesquisaTF.setFont(new java.awt.Font("SansSerif", 2, 16)); // NOI18N
        campoPesquisaTF.setText("   Pesquisar");
        campoPesquisaTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                campoPesquisaTFMouseClicked(evt);
            }
        });
        campoPesquisaTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoPesquisaTFActionPerformed(evt);
            }
        });
        campoPesquisaTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                campoPesquisaTFKeyReleased(evt);
            }
        });

        levelSL.setMaximum(5);
        levelSL.setPaintLabels(true);
        levelSL.setPaintTicks(true);
        levelSL.setSnapToTicks(true);
        levelSL.setToolTipText("");
        levelSL.setValue(0);
        levelSL.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                levelSLStateChanged(evt);
            }
        });
        levelSL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                levelSLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                levelSLMouseExited(evt);
            }
        });

        levelL.setText("Level: Debug");

        dataCHB.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        dataCHB.setText("Considerar datas");
        dataCHB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataCHBStateChanged(evt);
            }
        });
        dataCHB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataCHBMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataCHBMouseExited(evt);
            }
        });
        dataCHB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataCHBActionPerformed(evt);
            }
        });

        deL.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        deL.setText("De");

        dataInicialFTF.setEnabled(false);
        dataInicialFTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataInicialFTFMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataInicialFTFMouseExited(evt);
            }
        });
        dataInicialFTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataInicialFTFKeyReleased(evt);
            }
        });

        dataFinalFTF.setEnabled(false);
        dataFinalFTF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dataFinalFTFMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dataFinalFTFMouseExited(evt);
            }
        });
        dataFinalFTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataFinalFTFKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel3.setText("Até");

        filtrarB.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        filtrarB.setText("Filtrar");
        filtrarB.setEnabled(false);
        filtrarB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                filtrarBMouseEntered(evt);
            }
        });
        filtrarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtrarBActionPerformed(evt);
            }
        });

        ntaL.setText(".");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSeparator1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jSeparator1.setMinimumSize(new java.awt.Dimension(60, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(60, 10));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(ntaL, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pesquisaL1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(campoPesquisaTF, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(levelL)
                    .addComponent(levelSL, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(dataCHB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(deL)
                        .addGap(14, 14, 14)
                        .addComponent(dataInicialFTF, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(10, 10, 10)
                        .addComponent(dataFinalFTF, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(filtrarB)
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(deL)
                                        .addComponent(dataInicialFTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(7, 7, 7)
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(dataFinalFTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(dataCHB, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(filtrarB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(levelL)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(levelSL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(pesquisaL1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(campoPesquisaTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ntaL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                        .addGap(14, 14, 14))))
        );

        getContentPane().add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 150));

        worker2L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        worker2L.setToolTipText("");
        worker2L.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                worker2LMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                worker2LMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                worker2LMouseExited(evt);
            }
        });

        worker1L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        worker1L.setToolTipText("");
        worker1L.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                worker1LMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                worker1LMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                worker1LMouseExited(evt);
            }
        });

        WorkersL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        WorkersL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WorkersL.setText("Workers");

        homeL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeL.setToolTipText("");
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

        menuL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        menuL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuL.setText("Menu");

        configL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        configL.setToolTipText("");
        configL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                configLMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                configLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                configLMouseExited(evt);
            }
        });

        statusL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        statusL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusL.setText("Status");

        errosL.setText("Erros: 0");

        avisosL.setText("Avisos: 0");

        bemL.setText("Bem: 0");

        registrosL.setText("Registros: 0");

        worker2LL.setText("Worker2: NOK");

        worker1LL.setText("Worker1: NOK");

        maquinasL.setText("Máquinas: 0");

        changeL.setText("Atualização: 0");

        refreshL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        refreshL.setToolTipText("");
        refreshL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshLMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshLMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(241, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(worker1L, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(worker2L, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(WorkersL, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(homeL, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(configL, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(menuL, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(avisosL)
                            .addComponent(errosL)
                            .addComponent(bemL)
                            .addComponent(registrosL))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(worker2LL)
                            .addComponent(worker1LL)
                            .addComponent(maquinasL)
                            .addComponent(changeL))
                        .addGap(35, 35, 35)
                        .addComponent(refreshL, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(statusL, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(WorkersL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(menuL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(homeL, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(worker1L, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(worker2L, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(configL, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(registrosL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(errosL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(avisosL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bemL))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refreshL, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(maquinasL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(worker1LL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(worker2LL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeL)))))
                .addGap(23, 23, 23))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "Máquina", "Level", "Módulo", "Log", "ICMP Echo Request"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void worker2LMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker2LMouseClicked

    }//GEN-LAST:event_worker2LMouseClicked

    private void worker2LMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker2LMouseEntered
        worker2L.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_worker2LMouseEntered

    private void worker2LMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker2LMouseExited
        // TODO add your handling code here:
        worker2L.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_worker2LMouseExited

    private void worker1LMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker1LMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_worker1LMouseClicked

    private void worker1LMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker1LMouseEntered
        // TODO add your handling code here:
        worker1L.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_worker1LMouseEntered

    private void worker1LMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worker1LMouseExited
        // TODO add your handling code here:
        worker1L.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_worker1LMouseExited

    private void homeLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseClicked
        // TODO add your handling code here:
        this.dispose();
        new MainMenuForm().setVisible(true);
    }//GEN-LAST:event_homeLMouseClicked

    private void homeLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseEntered
        // TODO add your handling code here:
        homeL.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_homeLMouseEntered

    private void homeLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeLMouseExited
        // TODO add your handling code here:
        homeL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_homeLMouseExited

    private void configLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_configLMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_configLMouseClicked

    private void configLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_configLMouseEntered
        // TODO add your handling code here:
        configL.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_configLMouseEntered

    private void configLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_configLMouseExited
        // TODO add your handling code here:
        configL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_configLMouseExited

    private void campoPesquisaTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoPesquisaTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoPesquisaTFActionPerformed

    private void campoPesquisaTFMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_campoPesquisaTFMouseClicked
        if (isBarraPesquisaPrimeiroAcesso()) {
            campoPesquisaTF.setText("");
            campoPesquisaTF.setFont(new java.awt.Font("SansSerif", 4, 16));
            setBarraPesquisaPrimeiroAcesso(false);
        }
    }//GEN-LAST:event_campoPesquisaTFMouseClicked

    private void levelSLStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_levelSLStateChanged
        // TODO add your handling code here:
        System.out.println(levelSL.getValue());
        switch (levelSL.getValue()) {
            case 1:
                levelL.setText("Level: Fine");
                break;
            case 2:
                levelL.setText("Level: Info");
                break;
            case 3:
                levelL.setText("Level: Warning");
                break;
            case 4:
                levelL.setText("Level: Error");
                break;
            case 5:
                levelL.setText("Level: Severe");
                break;
            // ...
            default:
                levelL.setText("Level: Debug");
        }
        exibirInformacoesArray();
    }//GEN-LAST:event_levelSLStateChanged

    private void levelSLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_levelSLMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_levelSLMouseEntered

    private void levelSLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_levelSLMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_levelSLMouseExited

    private void dataCHBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dataCHBStateChanged
//        persistirInformacoes();
    }//GEN-LAST:event_dataCHBStateChanged

    private void dataCHBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataCHBMouseEntered
        // TODO add your handling code here:

    }//GEN-LAST:event_dataCHBMouseEntered

    private void dataCHBMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataCHBMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_dataCHBMouseExited

    private void dataCHBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataCHBActionPerformed
        // TODO add your handling code here:

        if (dataCHB.isSelected()) {
            dataInicialFTF.setEnabled(true);
            dataFinalFTF.setEnabled(true);
            filtrarB.setEnabled(true);
        } else {
            dataInicialFTF.setEnabled(false);
            dataFinalFTF.setEnabled(false);
            filtrarB.setEnabled(false);
        }
    }//GEN-LAST:event_dataCHBActionPerformed

    private void dataInicialFTFMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataInicialFTFMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_dataInicialFTFMouseEntered

    private void dataInicialFTFMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataInicialFTFMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_dataInicialFTFMouseExited

    private void dataInicialFTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataInicialFTFKeyReleased
    }//GEN-LAST:event_dataInicialFTFKeyReleased

    private void dataFinalFTFMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataFinalFTFMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_dataFinalFTFMouseEntered

    private void dataFinalFTFMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataFinalFTFMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_dataFinalFTFMouseExited

    private void dataFinalFTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataFinalFTFKeyReleased
    }//GEN-LAST:event_dataFinalFTFKeyReleased

    private void filtrarBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filtrarBMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_filtrarBMouseEntered

    private void filtrarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtrarBActionPerformed

        exibirInformacoesArray();
    }//GEN-LAST:event_filtrarBActionPerformed

    private void campoPesquisaTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campoPesquisaTFKeyReleased
        searchBarAction();
    }//GEN-LAST:event_campoPesquisaTFKeyReleased

    private void refreshLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshLMouseClicked
        // TODO add your handling code here:
        atualizaResultados();
    }//GEN-LAST:event_refreshLMouseClicked

    private void refreshLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshLMouseEntered
        // TODO add your handling code here:
        refreshL.setBorder(new RoundedBorder(Color.BLUE, 3, 20));
    }//GEN-LAST:event_refreshLMouseEntered

    private void refreshLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshLMouseExited
        refreshL.setBorder(new RoundedBorder(Color.LIGHT_GRAY, 1, 20));
    }//GEN-LAST:event_refreshLMouseExited

    private void atualizaResultados() {
        LogArray.clear();
        carregarInformacoesArquivo();
        exibirInformacoesArray();
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
            java.util.logging.Logger.getLogger(MonitoringUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitoringUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitoringUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitoringUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MonitoringUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel WorkersL;
    private javax.swing.JLabel avisosL;
    private javax.swing.JLabel bemL;
    private javax.swing.JTextField campoPesquisaTF;
    private javax.swing.JLabel changeL;
    private javax.swing.JLabel configL;
    private javax.swing.JCheckBox dataCHB;
    private javax.swing.JFormattedTextField dataFinalFTF;
    private javax.swing.JFormattedTextField dataInicialFTF;
    private javax.swing.JLabel deL;
    private javax.swing.JLabel errosL;
    private javax.swing.JButton filtrarB;
    private javax.swing.JLabel homeL;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel levelL;
    private javax.swing.JSlider levelSL;
    private javax.swing.JLabel maquinasL;
    private javax.swing.JLabel menuL;
    private javax.swing.JLabel ntaL;
    private javax.swing.JLabel pesquisaL1;
    private javax.swing.JLabel refreshL;
    private javax.swing.JLabel registrosL;
    private javax.swing.JLabel statusL;
    private javax.swing.JLabel worker1L;
    private javax.swing.JLabel worker1LL;
    private javax.swing.JLabel worker2L;
    private javax.swing.JLabel worker2LL;
    // End of variables declaration//GEN-END:variables
}
