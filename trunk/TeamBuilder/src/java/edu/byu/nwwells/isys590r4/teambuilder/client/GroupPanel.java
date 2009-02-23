
package edu.byu.nwwells.isys590r4.teambuilder.client;

import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author Nathan
 */
class GroupPanel extends AbsolutePanel implements HasDragHandle {

  Label title = new Label();
  Label delete = new Label();
  FlowPanel members = new FlowPanel();
  FlowPanelDropController dropGroupMember = new FlowPanelDropController(members);

  GroupPanel(String text) {
    title.setText(text);
    delete.setText("X");
    delete.setStylePrimaryName("group-panel-delete");
    delete.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {sender.getParent().setVisible(false);}
    });

    members.setPixelSize(130, 265);
    members.setStylePrimaryName("group-panel-members");


    setPixelSize(150, 300);
    add(title, 5, 5);
    add(delete, 130, 5);
    add(members, 5, 25);
    setStylePrimaryName("group-panel");
  }

  @Override
  public void setVisible(boolean visible) {
    if (!visible){
      //put members into MemberList
      for (int i = members.getWidgetCount()-1; i >= 0; i--){
        TeamBuilderEP.pnlMemberList.add(members.getWidget(i));
      }
      TeamBuilderEP.memberDragController.unregisterDropController(dropGroupMember);
      removeFromParent();
    }
    super.setVisible(visible);
  }

  public boolean isEmpty() {    return members.getWidgetCount() == 0;  }

  public Widget getDragHandle() {return title;}

}
