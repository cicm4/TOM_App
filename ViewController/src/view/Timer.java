package view;


import java.sql.Timestamp;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputDate;

import oracle.adf.view.rich.component.rich.nav.RichButton;
import oracle.adf.view.rich.event.PopupFetchEvent;

import java.util.Calendar;

import java.util.ServiceLoader;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.binding.BindingContainer;

import oracle.jbo.ValidationException;
import oracle.jbo.ViewObject;

import oracle.jbo.server.ViewObjectImpl;

import org.apache.myfaces.trinidad.event.LaunchEvent;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;


public class Timer {


    private RichTable taskTable;
    private RichInputDate start;
    private RichInputDate startTime;
    private RichButton startButton;
    private RichButton pauseButton;
    private RichButton finishButton;
    private RichButton cancelButton;
    private RichPopup myPopup;
    private boolean isDoneTask = false;

    public String startTask() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding TaskIter = (DCIteratorBinding) bindings.get("TasksViewLv1Iterator");
        String vl = TaskIter.getCurrentRow()
                            .getAttribute("Status")
                            .toString();
        if (vl.equals("0")) {
            try {
                String DateTask = TaskIter.getCurrentRow()
                                          .getAttribute("Starttime")
                                          .toString();
                System.out.println(DateTask);
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage message = new FacesMessage("Error, task has already begun");
                context.addMessage(null, message);
                RichPopup popup = getMyPopup();
                popup.hide();
            } catch (Exception e) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                TaskIter.getCurrentRow().setAttribute("Starttime", timestamp);
                commitIterator("TasksViewLv1Iterator");
                System.out.println(timestamp);
                RichPopup popup = getMyPopup();
                popup.hide();
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("Error, task has been finished");
            context.addMessage(null, message);
            RichPopup popup = getMyPopup();
            popup.hide();
        }
        return "a";
    }

    public String cancelTask() {
        RichPopup popup = getMyPopup();
        popup.hide();
        return "a";
    }

    public String pauseTask() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding TaskIter = (DCIteratorBinding) bindings.get("TasksViewLv1Iterator");
        String vl = TaskIter.getCurrentRow()
                            .getAttribute("Status")
                            .toString();
        if (vl.equals("0")) {
            try {
                DCIteratorBinding TaskIterOne = (DCIteratorBinding) bindings.get("UsersViewLv1Iterator");
                Timestamp DateTask = (Timestamp) TaskIter.getCurrentRow().getAttribute("Starttime");
                Timestamp CurrentDate = new Timestamp(System.currentTimeMillis());

                long longDateTask = DateTask.getTime();
                long longCurrentDate = CurrentDate.getTime();
                long longtimeSpent = longCurrentDate - longDateTask;
                longtimeSpent = longtimeSpent/60000;

                try {
                    String vs = TaskIterOne.getCurrentRow()
                                           .getAttribute("Timespent")
                                           .toString();
                    Long time = Long.parseLong(vs);
                    System.out.println("El tiempo cuando empezo la tara era: " + longDateTask);
                    System.out.println("El tiempo ahora es: " + longCurrentDate);
                    time += longtimeSpent;
                    TaskIterOne.getCurrentRow().setAttribute("Timespent", time);
                    TaskIter.getCurrentRow().setAttribute("Starttime", null);
                } catch (Exception e) {
                    TaskIterOne.getCurrentRow().setAttribute("Timespent", longtimeSpent);
                    TaskIter.getCurrentRow().setAttribute("Starttime", null);
                }
                commitIterator("TasksViewLv1Iterator");
                commitIterator("UsersViewLv1Iterator");

                RichPopup popup = getMyPopup();
                popup.hide();
           }
            catch (Exception e) {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage message = new FacesMessage("Error, task has is not being worked on");
                context.addMessage(null, message);
                RichPopup popup = getMyPopup();
                popup.hide();
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("Error, task has been finished");
            context.addMessage(null, message);
            RichPopup popup = getMyPopup();
            popup.hide();
        }
        return "a";
    }

    public String endTask() {
        pauseTask();
        System.out.println("end");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding TaskIter = (DCIteratorBinding) bindings.get("TasksViewLv1Iterator");
        TaskIter.getCurrentRow().setAttribute("Status", 1);
        commitIterator("TasksViewLv1Iterator");
        return "a";
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().toString();

    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void onPopup(PopupFetchEvent popupFetchEvent) {
        try {
            // Add event code here...
            System.out.println("Hola Mundo");
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding TaskIter = (DCIteratorBinding) bindings.get("TasksViewLv1Iterator");
            String DateTask = TaskIter.getCurrentRow()
                                      .getAttribute("Starttime")
                                      .toString();
            System.out.println(DateTask);
            /* RichButton startCButton = getStartButton();
            startCButton.setDisabled(true); */

        } catch (Exception e) {
            System.out.println("The date is null");

            /* RichButton pauseCButton = getPauseButton();
            pauseCButton.setDisabled(true);
            RichButton finishCButton = getFinishButton();
            finishCButton.setDisabled(true); */


        }
        System.out.println("Ya termine");
    }

    public void setTaskTable(RichTable taskTable) {
        this.taskTable = taskTable;
    }

    public RichTable getTaskTable() {
        return taskTable;
    }

    public void setStart(RichInputDate start) {
        this.start = start;
    }

    public RichInputDate getStart() {
        return start;
    }

    public void setStartTime(RichInputDate startTime) {
        this.startTime = startTime;
    }

    public RichInputDate getStartTime() {
        return startTime;
    }

    public void setStartButton(RichButton startButton) {
        this.startButton = startButton;
    }

    public RichButton getStartButton() {
        return startButton;
    }

    public void setPauseButton(RichButton pauseButton) {
        this.pauseButton = pauseButton;
    }

    public RichButton getPauseButton() {
        return pauseButton;
    }

    public void setFinishButton(RichButton finishButton) {
        this.finishButton = finishButton;
    }

    public RichButton getFinishButton() {
        return finishButton;
    }

    public void setCancelButton(RichButton cancelButton) {
        this.cancelButton = cancelButton;
    }

    public RichButton getCancelButton() {
        return cancelButton;
    }

    public void OpenButton(LaunchEvent launchEvent) {

    }

    public static ViewObjectImpl getViewObjectFromIterator(String nomIterator) {
        ViewObjectImpl returnVO = null;
        DCBindingContainer dcb = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        if (dcb != null) {
            DCIteratorBinding iter = dcb.findIteratorBinding(nomIterator);
            if (iter != null) {
                returnVO = (ViewObjectImpl) iter.getViewObject();
            }
        }
        return returnVO;
    }

    public void commitIterator(String IteratorName) {
        ViewObject vo = this.getViewObjectFromIterator(IteratorName);
        try {
            vo.getApplicationModule()
              .getTransaction()
              .validate();
            vo.getApplicationModule()
              .getTransaction()
              .commit();
        } catch (ValidationException e) {
            String validationErrorMessage = e.getDetailMessage();
            //Occur when some committed data is rejected due to validation error.
            //log it : log(Level.WARNING, " " + validationErrorMessage);
        } catch (Exception e) {
            //Log it and warn something unexpected occured
        }
    }

    public void setMyPopup(RichPopup myPopup) {
        this.myPopup = myPopup;
    }

    public RichPopup getMyPopup() {
        return myPopup;
    }
}

