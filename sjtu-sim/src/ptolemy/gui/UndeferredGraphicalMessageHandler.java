/* Singleton class for displaying exceptions, errors, warnings, and messages.

 Copyright (c) 1999-2010 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 PT_COPYRIGHT_VERSION_2
 COPYRIGHTENDKEY
 */
package ptolemy.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ptolemy.util.CancelException;
import ptolemy.util.MessageHandler;
import ptolemy.util.StringUtilities;

///////////////////////////////////////////////////////////////////
//// UndeferredGraphicalMessageHandler

/**
 This is a message handler that reports errors in a graphical dialog box.
 When an applet or application starts up, it should call setContext()
 to specify a component with respect to which the display window
 should be created.  This ensures that if the application is iconified
 or deiconified, that the display window goes with it. If the context
 is not specified, then the display window is centered on the screen,
 but iconifying and deiconifying may not work as desired.
 <p>

 <p>Note that to display a window with an error message, this graphical
 handler must be registered by calling
 {@link ptolemy.util.MessageHandler#setMessageHandler(MessageHandler)}.
 For example:
 <pre>
 GraphicalMessageHandler handler = new GraphicalMessageHandler();
 GraphicalMessageHandler.setMessageHandler(handler);
 GraphicalMessageHandler.error("My error", new Exception("My Exception"));
 </pre>
 If setMessageHandler() is not called, then the error() call will
 use the default handler and possibly display the message on standard error.

 <p>This class is based on (and contains code from) the diva GUIUtilities
 class.

 @author  Edward A. Lee, Steve Neuendorffer, John Reekie, and Elaine Cheong
 @version $Id: UndeferredGraphicalMessageHandler.java 57044 2010-01-27 22:41:05Z cxh $
 @since Ptolemy II 8.0
 @Pt.ProposedRating Yellow (eal)
 @Pt.AcceptedRating Red (reviewmoderator)
 */
public class UndeferredGraphicalMessageHandler extends MessageHandler {

    /** Get the component set by a call to setContext(), or null if none.
     *  @see #setContext(Component)
     *  @return The component with respect to which the display window
     *   is iconified, or null if none has been specified.
     */
    public static Component getContext() {
        if (_context == null) {
            return null;
        }

        return (Component) _context.get();
    }

    /** Set the component with respect to which the display window
     *  should be created.  This ensures that if the application is
     *  iconified or deiconified, that the display window goes with it.
     *  This is maintained in a weak reference so that the frame can be
     *  garbage collected.
     *  @see #getContext()
     *  @param context The component context.
     */
    public static void setContext(Component context) {
        // FIXME: This seems utterly incomplete...
        // We will inevitably have multiple frames,
        // so having one static context just doesn't
        // work.
        _context = new WeakReference(context);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /** Show the specified error message.
     *  This is deferred to execute in the swing event thread if it is
     *  called outside that thread.
     *  @param info The message.
     */
    protected void _error(String info) {
        Object[] message = new Object[1];
        String string = info;
        message[0] = _messageComponent(StringUtilities.ellipsis(string,
                StringUtilities.ELLIPSIS_LENGTH_SHORT));

        Object[] options = { "Dismiss" };

        // Show the MODAL dialog
        JOptionPane.showOptionDialog(getContext(), message, "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
                options, options[0]);
    }

    /** Show the specified message and throwable information.
     *  If the throwable is an instance of CancelException, then it
     *  is not shown.  By default, only the message of the throwable
     *  is thrown.  The stack trace information is only shown if the
     *  user clicks on the "Display Stack Trace" button.
     *  This is deferred to execute in the swing event thread if it is
     *  called outside that thread.
     *
     *  @param info The message.
     *  @param throwable The throwable.
     *  @see ptolemy.util.CancelException
     */
    protected void _error(String info, Throwable throwable) {
        if (throwable instanceof ptolemy.util.CancelException) {
            return;
        }

        // Sometimes you find that errors are reported
        // multiple times.  To find out who is calling
        // this method, uncomment the following.
        // System.out.println("------ reporting error:");
        // (new Throwable()).printStackTrace();
        Object[] message = new Object[1];
        String string;

        if (info != null) {
            string = info + "\n" + throwable.getMessage();
        } else {
            string = throwable.getMessage();
        }

        message[0] = _messageComponent(StringUtilities.ellipsis(string,
                StringUtilities.ELLIPSIS_LENGTH_SHORT));

        Object[] options = { "Dismiss", "Display Stack Trace" };

        // Show the MODAL dialog
        int selected = JOptionPane.showOptionDialog(getContext(), message,
                MessageHandler.shortDescription(throwable),
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
                options, options[0]);

        if (selected == 1) {
            _showStackTrace(throwable, info);
        }
    }

    /** Show the specified message in a modal dialog.
     *  This is deferred to execute in the swing event thread if it is
     *  called outside that thread.
     *  @param info The message.
     */
    protected void _message(String info) {
        Object[] message = new Object[1];
        message[0] = _messageComponent(StringUtilities.ellipsis(info,
                StringUtilities.ELLIPSIS_LENGTH_LONG));

        Object[] options = { "OK" };

        // Show the MODAL dialog
        JOptionPane.showOptionDialog(getContext(), message, "Message",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
    }

    /** Show the specified message in a modal dialog.  If the user
     *  clicks on the "Cancel" button, then throw an exception.
     *  This gives the user the option of not continuing the
     *  execution, something that is particularly useful if continuing
     *  execution will result in repeated warnings.
     *  NOTE: If this is called outside the swing event thread, then
     *  no cancel button is presented and no CancelException will be
     *  thrown.  This is because the displaying of the message must
     *  be deferred to the swing event thread, according to the swing
     *  architecture, or we could get deadlock or rendering problems.
     *  @param info The message.
     *  @exception ptolemy.util.CancelException If the user clicks on the
     * "Cancel" button.
     */
    protected void _warning(String info) throws CancelException {
        Object[] options = { "OK", "Cancel" };
        Object[] message = new Object[1];

        // If the message lines are longer than 80 characters, we split it
        // into shorter new line separated strings.
        // Running vergil on a HSIF .xml file will create a line longer
        // than 80 characters
        message[0] = _messageComponent(StringUtilities.ellipsis(info,
                StringUtilities.ELLIPSIS_LENGTH_LONG));

        // Show the MODAL dialog
        int selected = JOptionPane.showOptionDialog(getContext(), message,
                "Warning", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (selected == 1) {
            throw new ptolemy.util.CancelException();
        }
    }

    /** Show the specified message and throwable information
     *  in a modal dialog.  If the user
     *  clicks on the "Cancel" button, then throw an exception.
     *  This gives the user the option of not continuing the
     *  execution, something that is particularly useful if continuing
     *  execution will result in repeated warnings.
     *  By default, only the message of the throwable
     *  is shown.  The stack trace information is only shown if the
     *  user clicks on the "Display Stack Trace" button.
     *  NOTE: If this is called outside the swing event thread, then
     *  no cancel button is presented and no CancelException will be
     *  thrown.  This is because the displaying of the message must
     *  be deferred to the swing event thread, according to the swing
     *  architecture, or we could get deadlock or rendering problems.
     *  @param info The message.
     *  @param throwable The throwable.
     *  @exception ptolemy.util.CancelException If the user clicks on the
     *  "Cancel" button.
     */
    protected void _warning(String info, Throwable throwable)
            throws CancelException {
        Object[] message = new Object[1];
        message[0] = _messageComponent(StringUtilities.ellipsis(info,
                StringUtilities.ELLIPSIS_LENGTH_LONG));

        Object[] options = { "OK", "Display Stack Trace", "Cancel" };

        // Show the MODAL dialog
        int selected = JOptionPane.showOptionDialog(getContext(), message,
                "Warning", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (selected == 1) {
            _showStackTrace(throwable, info);
        } else if (selected == 2) {
            throw new ptolemy.util.CancelException();
        }
    }

    /** Ask the user a yes/no question, and return true if the answer
     *  is yes.
     *
     *  @param question The yes/no question.
     *  @return True if the answer is yes.
     */
    protected boolean _yesNoQuestion(String question) {
        Object[] message = new Object[1];
        message[0] = _messageComponent(StringUtilities.ellipsis(question,
                StringUtilities.ELLIPSIS_LENGTH_LONG));

        Object[] options = { "Yes", "No" };

        // Show the MODAL dialog
        int selected = JOptionPane.showOptionDialog(getContext(), message,
                "Warning", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (selected == 0) {
            return true;
        } else {
            return false;
        }
    }

    /** Ask the user a yes/no/cancel question, and return true if the answer
     *  is yes.
     *
     *  @param question The yes/no/cancel question.
     *  @return True if the answer is yes.
     *  @exception ptolemy.util.CancelException If the user clicks on
     *  the "Cancel" button.
     */
    protected boolean _yesNoCancelQuestion(String question)
            throws CancelException {
        Object[] message = new Object[1];
        message[0] = _messageComponent(StringUtilities.ellipsis(question,
                StringUtilities.ELLIPSIS_LENGTH_LONG));

        Object[] options = { "Yes", "No", "Cancel" };

        // Show the MODAL dialog
        int selected = JOptionPane.showOptionDialog(getContext(), message,
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (selected == 0) {
            return true;
        } else if (selected == 2) {
            throw new ptolemy.util.CancelException();
        } else {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /** Display a stack trace dialog. The "info" argument is a
     *  string printed at the top of the dialog instead of the Throwable
     *  message.
     *  @param throwable The throwable.
     *  @param info A message.
     */
    protected void _showStackTrace(Throwable throwable, String info) {
        // FIXME: Eventually, the dialog should
        // be able to email us a bug report.
        // Show the stack trace in a scrollable text area.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);

        JTextArea text = new JTextArea(sw.toString(), 60, 80);
        JScrollPane stext = new JScrollPane(text);
        stext.setPreferredSize(new Dimension(600, 300));
        text.setCaretPosition(0);
        text.setEditable(false);

        // We want to stack the text area with another message
        Object[] message = new Object[2];
        String string;

        if (info != null) {
            string = info + "\n" + throwable.getMessage();
        } else {
            string = throwable.getMessage();
        }

        message[0] = _messageComponent(StringUtilities.ellipsis(string,
                StringUtilities.ELLIPSIS_LENGTH_LONG));
        message[1] = stext;

        // Show the MODAL dialog
        JOptionPane.showMessageDialog(getContext(), message, "Stack trace",
                JOptionPane.ERROR_MESSAGE);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    /** The context. */
    protected static WeakReference _context = null;

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    // Return an Object suitable for JOptionPane so that the user
    // can select it.
    // We return a JTextField that looks like a JLabel, but is
    // selectable.  Many thanks to:
    // http://www.rgagnon.com/javadetails/java-0296.html
    private Object _messageComponent(String message) {
        // It is nice if error messages have selectable text.
        // See http://bugzilla.ecoinformatics.org/show_bug.cgi?id=4147
        Object result = message;
        // Unfortunately, this hack does not wrap text properly,
        // so I'm commenting it out temporarily.
        //         try {
        //             JTextField textField = new JTextField(message);
        //             textField.setEditable(false);
        //             textField.setBorder(null);
        //             textField.setForeground(UIManager.getColor("Label.foreground"));
        //             textField.setBackground(UIManager.getColor("Label.background"));
        //             textField.setFont(UIManager.getFont("Label.font"));
        //             result = textField;
        //         } catch (Exception ex) {
        //             // Ignore, just return the string
        //         }
        return result;
    }
}
