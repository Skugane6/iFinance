package se2203b.assignments.ifinance;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


public class AccountGroupsController implements Initializable {

    @FXML
    private Button exitBtn;

    @FXML
    private TextField groupNameField;

    @FXML
    private Button saveBtn;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TitledPane modifyGroupPane;
    private String flag;
    private String cGroupName;
    private TreeItem<String> currentTreeItem;

    // Create a context menu with CRUD options
    MenuItem add = new MenuItem("Add New Group");
    MenuItem modify = new MenuItem("Change Group Name");
    MenuItem delete = new MenuItem("Delete Group");
    ContextMenu contextMenu = new ContextMenu(add, modify, delete);

    ArrayList<String> list = new ArrayList<>(Arrays.asList("Assets", "Liabilities", "Income", "Expenses"));

    private Connection conn;

    private AccountGroupsAdapter accountGroupsAdapter;
    private AccountCategoryAdapter accountCategoryAdapter;

    public void setUserModel(AccountGroupsAdapter accGroups,AccountCategoryAdapter accCat){
        accountGroupsAdapter = accGroups;
        accountCategoryAdapter = accCat;
    }

    public void setIFinanceController(IFinanceController controller){
        IFinanceController iFinanceController = controller;
    }

    @FXML
    void exit(ActionEvent event) {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(ActionEvent event) throws SQLException {
        if(flag == "add") {
            int parent;
            String element;
            if (AccountGroupsAdapter.getID(cGroupName)!=0){
                parent = AccountGroupsAdapter.getID(cGroupName);
                element = AccountGroupsAdapter.getElement(cGroupName);
            } else{
                parent = 0;
                element = cGroupName;
            }
            AccountGroupsAdapter.createGroup(groupNameField.getText(), parent, element);
            findTreeItem(treeView.getRoot(), cGroupName).getChildren().add(new TreeItem<>(groupNameField.getText()));
        }
        else if(flag == "modify"){
            AccountGroupsAdapter.updateGroup(cGroupName, groupNameField.getText());
            findTreeItem(treeView.getRoot(), cGroupName).setValue(groupNameField.getText());
            treeView.setShowRoot(true);
            treeView.setShowRoot(false);
        }
        treeView.getSelectionModel().select(findTreeItem(treeView.getRoot(),groupNameField.getText()));
        groupNameField.clear();
        groupNameField.setDisable(true);
        modifyGroupPane.setDisable(true);
    }


    @FXML
    public void selectItem(ContextMenuEvent contextMenuEvent) {
        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
        final boolean[] canEdit = {true};
        final boolean[] canDelete = {true};
        final boolean[] canAdd = {true};

        // Register an event handler for mouse clicks on the tree view
        treeView.setOnMouseClicked((MouseEvent event) -> {
            if (!treeView.getSelectionModel().getSelectedItem().equals(item)) {
                contextMenu.hide();
            }
                // Check if the right mouse button was clicked
                if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    if (list.contains(item.getValue())) {
                        canEdit[0] = false;
                        canDelete[0] = false;
                        canAdd[0] = true;
                    }
                    else if (!item.getChildren().isEmpty()) {
                        canEdit[0] = true;
                        canDelete[0] = false;
                        canAdd[0] = true;
                    }
                    modify.setDisable(!canEdit[0]);
                    delete.setDisable(!canDelete[0]);
                    add.setDisable(!canAdd[0]);
                    // Show the context menu at the mouse position
                    contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
                }
        });

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String id = ((MenuItem) event.getSource()).getText();
                switch (id) {
                    case "Add New Group":
                        flag = "add";
                        cGroupName = item.getValue();
                        currentTreeItem = item;
                        modifyGroupPane.setDisable(false);
                        groupNameField.setDisable(false);
                        groupNameField.requestFocus();
                        break;
                    case "Change Group Name":
                        flag = "modify";
                        cGroupName = item.getValue();
                        currentTreeItem = item;
                        modifyGroupPane.setDisable(false);
                        groupNameField.setDisable(false);
                        groupNameField.requestFocus();
                        break;
                    case "Delete Group":
                        cGroupName = item.getValue();
                        try {
                            AccountGroupsAdapter.deleteGroup(AccountGroupsAdapter.getID(cGroupName));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        removeAndHideTreeItem(item,treeView);
                        break;
                    default:
                        break;
                }
            }
        };
        add.setOnAction(eventHandler);
        modify.setOnAction(eventHandler);
        delete.setOnAction(eventHandler);
    }

    public TreeItem<String> findTreeItem(TreeItem<String> root, String value) {
        if (root.getValue().equals(value)) {
            return root;
        }

        for (TreeItem<String> child : root.getChildren()) {
            TreeItem<String> result = findTreeItem(child, value);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public boolean isTreeItemExists(TreeItem<String> item, TreeView<String> treeView) {
        ObservableList<TreeItem<String>> items = treeView.getRoot().getChildren();
        for (TreeItem<String> currentItem : items) {
            if (currentItem.equals(item)) {
                return true;
            } else {
                boolean result = isChildExists(currentItem, item);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isChildExists(TreeItem<String> currentItem, TreeItem<String> item) {
        if (currentItem.equals(item)) {
            return true;
        } else {
            ObservableList<TreeItem<String>> children = currentItem.getChildren();
            for (TreeItem<String> child : children) {
                boolean result = isChildExists(child, item);
                if (result) {
                    return true;
                }
            }
            return false;
        }
    }

    public void removeAndHideTreeItem(TreeItem<String> item, TreeView<String> treeView) {
        TreeItem<String> parent = item.getParent();
        if (parent != null) {
            parent.getChildren().remove(item);
            treeView.setShowRoot(true);
            treeView.setShowRoot(false);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupNameField.setDisable(true);
        modifyGroupPane.setDisable(true);
        populateTree();
    }

    public void populateTree() {
        TreeItem<String> assetsBranch = new TreeItem<>("Assets");
        TreeItem<String> liabilitiesBranch = new TreeItem<>("Liabilities");
        TreeItem<String> incomeBranch = new TreeItem<>("Income");
        TreeItem<String> expensesBranch = new TreeItem<>("Expenses");

        treeView.setRoot(new TreeItem<>("dummy"));
        treeView.setShowRoot(false);
        treeView.getRoot().getChildren().addAll(assetsBranch, liabilitiesBranch, incomeBranch, expensesBranch);


        ArrayList<Group> grpList = null;
        try {
            grpList = AccountGroupsAdapter.GroupsList();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (Group group : grpList) {
            TreeItem<String> parentRt = null;
            if (group.getElement().getName() != null) {
                if (group.getElement().getName().equals("Assets")) {
                    parentRt = assetsBranch;
                }
                if (group.getElement().getName().equals("Liabilities")) {
                    parentRt = liabilitiesBranch;
                }
                if (group.getElement().getName().equals("Income")) {
                    parentRt = incomeBranch;
                }
                if (group.getElement().getName().equals("Expenses")) {
                    parentRt = expensesBranch;
                }
                if (group.getParent()!=null) {
                    TreeItem<String> innerParentRt = findTreeItem(treeView.getRoot(),group.getParent().getName());
                    parentRt = innerParentRt;
                }
                parentRt.getChildren().add(new TreeItem<>(group.getName()));

            }
        }
    }

}