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
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TeamBuilderEP implements EntryPoint {
  static final int MEMBER_PANEL_WIDTH = 150;

  AbsolutePanel boundaryPanel = new AbsolutePanel();
  AbsolutePanel pnlMember = new AbsolutePanel();
  Label lblMember = new Label();
  FlowPanel pnlMemberList = new FlowPanel();
  TextBox txtMember = new TextBox();
  FlowPanelDropController pnlMemberListDC = new FlowPanelDropController(pnlMemberList);
  AbsolutePanel pnlGroup = new AbsolutePanel();
  Label lblGroup = new Label();
  DockPanel pnlGroupControl = new DockPanel();
  Button btnAddGroup = new Button("Add Group");
  Button btnRemoveEmpty = new Button("Remove Empty");
  Button btnAssignMembers = new Button("Assign All");
  CheckBox chkBalancedAssign = new CheckBox("Balanced Assign");
  Label lblGroupControl = new Label();
  AbsolutePanel pnlGroupArea = new AbsolutePanel();

  PickupDragController memberDragController = new PickupDragController(boundaryPanel, false);
  PickupDragController groupDragController = new PickupDragController(pnlGroupArea, false);


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
        pnlGroup.setPixelSize(boundaryPanel.getOffsetWidth()-MEMBER_PANEL_WIDTH, boundaryPanel.getOffsetHeight());
      }});

    // Create a boundary panel to constrain all drag operations
    boundaryPanel.setPixelSize(Window.getClientWidth()-20, Window.getClientHeight()-20);
    boundaryPanel.addStyleName("app-wrapper");

    initMember();
    initGroup();

    RootPanel.get().add(boundaryPanel);

  }


  private void initMember() {
    boundaryPanel.add(pnlMember);
    pnlMember.addStyleName("pnlParent");
    pnlMember.add(lblMember);
    lblMember.setText("Members");
    pnlMember.setPixelSize(MEMBER_PANEL_WIDTH, Window.getClientHeight() - 20);
    pnlMemberList.setStyleName("pnlMemberList");
    pnlMember.add(pnlMemberList);
    pnlMemberList.setPixelSize(MEMBER_PANEL_WIDTH, 1000);
    txtMember.setStyleName("txtMember");
    txtMember.setWidth(MEMBER_PANEL_WIDTH - 10 + "px");
    txtMember.addKeyboardListener(new KeyboardListenerAdapter() {

      @Override
      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if (keyCode == (char) KeyboardListener.KEY_ENTER) {
          Label newMember = new Label();
          newMember.setText(txtMember.getText());
          pnlMemberList.add(newMember);
          memberDragController.makeDraggable(newMember, newMember);
          txtMember.setText("");
        }
      }
    });
    pnlMemberList.add(txtMember);
    memberDragController.registerDropController(pnlMemberListDC);
  }

  private void initGroup() {
    pnlGroup.addStyleName("pnlParent");
    pnlGroup.setPixelSize(boundaryPanel.getOffsetWidth()-MEMBER_PANEL_WIDTH, boundaryPanel.getOffsetHeight());
    pnlGroup.add(lblGroup);
    lblGroup.setText("Teams");

    pnlGroup.add(pnlGroupControl);
    pnlGroupControl.addStyleName("pnlGroupControl");
    pnlGroupControl.add(btnAddGroup, DockPanel.WEST);
    pnlGroupControl.add(btnRemoveEmpty, DockPanel.WEST);
    pnlGroupControl.add(chkBalancedAssign, DockPanel.EAST);
    pnlGroupControl.add(btnAssignMembers, DockPanel.EAST);
    pnlGroupControl.add(lblGroupControl, DockPanel.CENTER);
    lblGroupControl.setText("Controls");
    
    boundaryPanel.add(pnlGroup,MEMBER_PANEL_WIDTH, 0);
  }

}//EntryPoint