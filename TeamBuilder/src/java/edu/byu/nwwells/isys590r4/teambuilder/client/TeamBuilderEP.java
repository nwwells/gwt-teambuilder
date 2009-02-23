/*
 * TeamBuilderEP.java
 *
 * Created on February 21, 2009, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.byu.nwwells.isys590r4.teambuilder.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TeamBuilderEP implements EntryPoint {
  static final int MEMBER_PANEL_WIDTH = 150;
  static final String TXT_MEMBER_DEFAULT = "Add...";

  static AbsolutePanel boundaryPanel = new AbsolutePanel();
  static AbsolutePanel pnlMember = new AbsolutePanel();
  static Label lblMember = new Label();
  static FlowPanel pnlMemberList = new FlowPanel();
  static TextBox txtMember = new TextBox();
  static AbsolutePanel pnlGroup = new AbsolutePanel();
  static Label lblGroup = new Label();
  static DockPanel pnlGroupControl = new DockPanel();
  static Button btnAddGroup = new Button("Add Group");
  static Button btnRemoveEmpty = new Button("Remove Empty");
  static Button btnAssignMembers = new Button("Assign All");
  static CheckBox chkBalancedAssign = new CheckBox("Balanced");
  static Label lblGroupControl = new Label();
  static AbsolutePanel pnlGroupArea = new AbsolutePanel();
  
  static PopupPanel pnlAddGroup = new PopupPanel(true, false);
  static TextBox txtGroup = new TextBox();

  static PickupDragController memberDragController = new PickupDragController(boundaryPanel, false);
  static PickupDragController groupDragController = new PickupDragController(pnlGroupArea, false);
  static AbsolutePositionDropController dropGroupArea = new AbsolutePositionDropController(pnlGroupArea);
  static FlowPanelDropController dropMemberList = new FlowPanelDropController(pnlMemberList);

  public void onModuleLoad() {
    // set uncaught exception handler
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable throwable) {
        String text = "Uncaught exception: ";
        while (throwable != null) {
          StackTraceElement[] stackTraceElements = throwable.getStackTrace();
          text += throwable.toString() + "\n";
          for (int i = 0; i < stackTraceElements.length; i++) {
            text += "    at " + stackTraceElements[i] + "\n";
          }
          throwable = throwable.getCause();
          if (throwable != null) {
            text += "Caused by: ";
          }
        }
        DialogBox dialogBox = new DialogBox(true);
        DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
        System.err.print(text);
        text = text.replaceAll(" ", " ");
        dialogBox.setHTML("<pre>" + text + "</pre>");
        dialogBox.center();
      }
    });

    // use a deferred command so that the handler catches onModuleLoad2() exceptions
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        onModuleLoad2();
      }
    });
  }


  private void onModuleLoad2() {
    //Resize boundary when window is resized.
    Window.addWindowResizeListener(new WindowResizeListener() {
      public void onWindowResized(int width, int height) {
        boundaryPanel.setPixelSize(width-20, height-20);
        pnlGroup.setPixelSize(boundaryPanel.getOffsetWidth()-MEMBER_PANEL_WIDTH-10, boundaryPanel.getOffsetHeight());
      }});

    // Create a boundary panel to constrain all drag operations
    boundaryPanel.setPixelSize(Window.getClientWidth()-20, Window.getClientHeight()-20);
    boundaryPanel.addStyleName("app-wrapper");

    initMember();
    initGroup();
    initListeners();

    //Register Drop Controllers
    groupDragController.registerDropController(dropGroupArea);
    memberDragController.registerDropController(dropMemberList);

    //Add boundaryPanel to RootPanel
    RootPanel.get().add(boundaryPanel);

  }

  private void initListeners() {
    //Keyboard Listener for txtMember to create a member
    txtMember.addKeyboardListener(new KeyboardListenerAdapter() {
      @Override
      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (keyCode == (char) KeyboardListener.KEY_ENTER) {
          Label newMember = new Label();
          newMember.setText(txtMember.getText());
          pnlMemberList.add(newMember);
          memberDragController.makeDraggable(newMember, newMember);
          txtMember.setText("");
        }}
    });

    //MouseEvent to add and remove "Add..." to/from txtMember
    txtMember.addFocusListener(new FocusListener() {
      public void onFocus(Widget sender)
        {if (txtMember.getText().equals(TXT_MEMBER_DEFAULT)) txtMember.setText("");}
      public void onLostFocus(Widget sender)
        {if (txtMember.getText().equals("")) txtMember.setText(TXT_MEMBER_DEFAULT);}
    });

    //Add Group Listener
    //add listener to show popup
    btnAddGroup.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        pnlAddGroup = new PopupPanel();
        pnlAddGroup.add(txtGroup);
        pnlAddGroup.setStylePrimaryName("popup-panel");
        pnlAddGroup.show();
        pnlAddGroup.setPopupPosition(btnAddGroup.getAbsoluteLeft(), btnAddGroup.getAbsoluteTop());
      }
    });
    //add listener to get Input
    txtGroup.addKeyboardListener(new KeyboardListenerAdapter() {
      @Override
      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (keyCode == (char) KeyboardListener.KEY_ENTER) {
          GroupPanel gp = new GroupPanel(txtGroup.getText());
          pnlGroupArea.add(
                  gp,
                  Random.nextInt(pnlGroupArea.getOffsetWidth()-150),
                  Random.nextInt(pnlGroupArea.getOffsetHeight()-300));
          groupDragController.makeDraggable(gp);
          memberDragController.registerDropController(gp.dropGroupMember);
          txtGroup.setText("");
          pnlAddGroup.setVisible(false);
        }}
    });

    //Remove Empties Listener
    btnRemoveEmpty.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        pnlGroupArea.getWidgetCount();
        for (int i = 0; i < pnlGroupArea.getWidgetCount(); i++){
          if (pnlGroupArea.getWidget(i) instanceof GroupPanel &&
             ((GroupPanel)pnlGroupArea.getWidget(i)).isEmpty())
                pnlGroupArea.getWidget(i).setVisible(false);
        }}
    });

    //Assign All Listener
    btnAssignMembers.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        while (pnlMemberList.getWidgetCount() > 0){
          for (int j = pnlGroupArea.getWidgetCount()-1; j >= 0; j--){
            if (pnlMemberList.getWidgetCount() > 0)
              ((GroupPanel)pnlGroupArea.getWidget(j)).members.add(pnlMemberList.getWidget(pnlMemberList.getWidgetCount()-1));
          }}}
    });

  }

  private void initMember() {
    boundaryPanel.add(pnlMember);
    pnlMember.addStyleName("pnlParent");
    pnlMember.add(lblMember);
    lblMember.setText("Members");
    pnlMemberList.setStyleName("pnlMemberList");
    pnlMember.add(txtMember);
    pnlMember.add(pnlMemberList);
    txtMember.setStyleName("txtMember");
    txtMember.setText(TXT_MEMBER_DEFAULT);
    memberDragController.registerDropController(dropMemberList);
    pnlMember.setPixelSize(MEMBER_PANEL_WIDTH, Window.getClientHeight() - 20);
    pnlMemberList.setPixelSize(MEMBER_PANEL_WIDTH, 1000);
    txtMember.setWidth(MEMBER_PANEL_WIDTH - 10 + "px");
  }

  private void initGroup() {
    boundaryPanel.add(pnlGroup,MEMBER_PANEL_WIDTH, 0);
    pnlGroup.addStyleName("pnlParent");
    pnlGroup.add(lblGroup);
    lblGroup.setText("Teams");

    //setup Group Controls
    pnlGroup.add(pnlGroupControl);
    pnlGroupControl.addStyleName("pnlGroupControl");
    pnlGroupControl.add(btnAddGroup, DockPanel.LINE_START);
    btnAddGroup.addStyleName("ctrlGroup");
    pnlGroupControl.add(btnRemoveEmpty, DockPanel.LINE_START);
    btnRemoveEmpty.addStyleName("ctrlGroup");
    pnlGroupControl.add(chkBalancedAssign, DockPanel.LINE_END);
    pnlGroupControl.setCellVerticalAlignment(chkBalancedAssign, DockPanel.ALIGN_MIDDLE);
    pnlGroupControl.add(btnAssignMembers, DockPanel.LINE_END);
    btnAssignMembers.addStyleName("ctrlGroup");
    pnlGroupControl.add(lblGroupControl, DockPanel.CENTER);
    pnlGroupControl.setCellVerticalAlignment(lblGroupControl, DockPanel.ALIGN_MIDDLE);
    lblGroupControl.setText("Controls");
    lblGroupControl.setVisible(false);
    pnlGroupControl.setCellWidth(lblGroupControl, "55%");

    //setup Group Area
    pnlGroupArea.setStylePrimaryName("pnlGroupArea");
    pnlGroup.add(
            pnlGroupArea,
            pnlGroup.getAbsoluteLeft() + 5,
            pnlGroup.getAbsoluteTop() + lblGroup.getOffsetHeight() + pnlGroupControl.getOffsetHeight() + 63);

    //Set Panel Sizes
    pnlGroup.setPixelSize(
            Window.getClientWidth() - (MEMBER_PANEL_WIDTH + 10),
            1000);
    pnlGroupControl.setPixelSize(
            Window.getClientWidth()-(MEMBER_PANEL_WIDTH + 25),
            btnAssignMembers.getOffsetHeight());
    pnlGroupArea.setPixelSize(
            Window.getClientWidth() - (MEMBER_PANEL_WIDTH + 25),
            Window.getClientHeight() - (lblGroup.getOffsetHeight() + pnlGroupControl.getOffsetHeight()));
    
  }

}//EntryPoint