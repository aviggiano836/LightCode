/**
 * Created by Ariel on 2/25/2017.
 */
import com.sun.webkit.ColorChooser;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static java.lang.Integer.parseInt;

public class gui extends Application {
    private final static int window_height = 800;
    private final static int window_width = 1000;
    private final static int rect_height = 60;
    private final static int rect_width = 300;
    private final static int startX = 20;
    private int startY = 20;
    private final static int x = window_width - rect_width;
    private final static int y = 0;
    private final static int tick = 20;
    private Group group;
    private ArrayList<StackPane> code;
    private ArrayList<String> block;
    private Stage primaryStage;
    private final Rectangle canvas = new Rectangle(window_width, window_height);
    private final Rectangle commandSpace = new Rectangle(rect_width + 40, window_height);
    private double initY;
    private double initX;
    private Point2D dragAnchor;
    private int i;
    private int lastport;
    private StackPane current;

    @Override
    /***
     * calls all necessary functions to set the stage
     */
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        group = new Group();
        Scene scene = new Scene(createCommands());
        code = new ArrayList<>();
        group.getChildren().add(save());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /***
     *
     * @return a save button that calls getFilePath()
     */
    private Button save() {
        Button b = new Button("Save");
        b.setTranslateX(window_width - b.getWidth() - 60);
        b.setTranslateY(window_height - b.getHeight() - 40);
        b.setBackground(new Background(new BackgroundFill(Color.CORAL, CornerRadii.EMPTY,
                Insets.EMPTY)));
        b.setOnMouseClicked((MouseEvent me) -> {
            writeFile();
        });
        return b;
    }

    /***
     *
     * @return an ArrayList of all the ProgNodes in the file
     *                      checks that the last item in the file is a Halt block and that the file is not empty
     *                          returns null otherwise
     */
    private ArrayList<String> writeCode() {
        code.sort(new commandComparator());
        if (code.size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<String> block = new ArrayList<>();
        block.add("#include <Adafruit_NeoPixel.h>");
        block.add("      #define PIN 7");
        block.add("//Parameter 1 = number of pixels");
        block.add("//Parameter 2 = pin number");
        block.add("//Parameter 3 = pixel type flags");
        block.add("Adafruit_NeoPixel strip = Adafruit_NeoPixel(2, PIN, NEO_GRB + NEO_KHZ800);");
        block.add("void setup() {");
        block.add("  strip.begin();");
        block.add("  strip.show();");
        block.add("}");
        block.add("");
        block.add("void loop() {");
        block.add("//YOUR CODE");


        for (i = 0; i < code.size(); i++) {
            StackPane s = code.get(i);
            String com = s.getAccessibleText();
            if (com.equals("Set Flower #")) {
                String number =((TextField)((HBox) s.getChildren().get(1)).getChildren().get(1)).getText();
                if (number == null){
                    errorPopup("Please put in the number flower you wish to set");
                }
                lastport = parseInt(number) - 1;
                block.add("strip.setPixelColor(" + lastport + ", 200, 200, 200);");
            } else if (com.equals("Show for ")) {
                String time = ((TextField) ((HBox)s.getChildren().get(1)).getChildren().get(1)).getText();
                if (time == null){
                    errorPopup("Please put in a time for the \"Show for\" Block");
                }
                block.add("delay(" + time + ");");
                block.add("strip.setPixelColor(" + lastport + ", 0, 0, 0, 0)");
            } else if (com.equals("If ") || (com.equals("While "))) {
                String sensors = ((String)((ComboBox)((HBox) s.getChildren().get(1)).getChildren().get(1)).getValue());
                String condition = ((String)((ComboBox)((HBox) s.getChildren().get(1)).getChildren().get(2)).getValue());
                String number = ((TextField)((HBox) s.getChildren().get(1)).getChildren().get(3)).getText();
                if ((sensors == null) || (condition == null) || (number == null)){
                    errorPopup("Please finish filling out " + com + "the block");
                }
                block.add(com + "(analogRead(0)" + condition + number + "){");
                if (code.get(i+1).getAccessibleText().equals("Begin")){
                    getCodeBlock(code.subList(i+2, code.size()));
                } else {
                    errorPopup("Please follow each If, While, and For statement by a \"Begin\" block");
                }
                block.add("}");
            } else if (com.equals("Else ")) {
                block.add("Else {");
                block.add("}");
            } else if (com.equals("For ")) {
                String number = ((TextField) s.getChildren().get(3)).getText();
                if (number == null){
                    errorPopup("Please number for the \"For\" block");
                }
                block.add("for(int i = 0; i < " + number + "; i++){");
                if (code.get(i+1).getAccessibleText().equals("Begin")){
                    getCodeBlock(code.subList(i+2, code.size()));
                } else {
                    errorPopup("Please follow each If, While, and For statement by a \"Begin\" block");
                }
                block.add("}");
            }
        }
        block.add("}");
        block.add("}");
        return block;
    }

    private void getCodeBlock(List<StackPane> start) {
        for (i = 0; i < code.size(); i++) {
            StackPane s = code.get(i);
            String com = s.getAccessibleText();
            if (com.equals("Set Flower #")) {
                String number =((TextField)((HBox) s.getChildren().get(1)).getChildren().get(1)).getText();
                if (number == null){
                    errorPopup("Please put in the number flower you wish to set");
                }
                lastport = parseInt(number) - 1;
                block.add("strip.setPixelColor(" + lastport + ", 200, 200, 200);");
            } else if (com.equals("Show for ")) {
                String time = ((TextField) ((HBox)s.getChildren().get(1)).getChildren().get(1)).getText();
                if (time == null){
                    errorPopup("Please put in a time for the \"Show for\" Block");
                }
                block.add("delay(" + time + ");");
                block.add("strip.setPixelColor(" + lastport + ", 0, 0, 0, 0)");
            } else if (com.equals("If ") || (com.equals("While "))) {
                String sensors = ((String)((ComboBox)((HBox) s.getChildren().get(1)).getChildren().get(1)).getValue());
                String condition = ((String)((ComboBox)((HBox) s.getChildren().get(1)).getChildren().get(2)).getValue());
                String number = ((TextField)((HBox) s.getChildren().get(1)).getChildren().get(3)).getText();
                if ((sensors == null) || (condition == null) || (number == null)){
                    errorPopup("Please finish filling out " + com + "the block");
                }
                block.add(com + "(analogRead(0)" + condition + number + "){");
                if (code.get(i+1).getAccessibleText().equals("Begin")){
                    getCodeBlock(code.subList(i+2, code.size()));
                } else {
                    errorPopup("Please follow each If, While, and For statement by a \"Begin\" block");
                }
                block.add("}");
            } else if (com.equals("Else ")) {
                block.add("Else {");
                block.add("}");
            } else if (com.equals("For ")) {
                String number = ((TextField) s.getChildren().get(3)).getText();
                if (number == null){
                    errorPopup("Please number for the \"For\" block");
                }
                block.add("for(int i = 0; i < " + number + "; i++){");
                if (code.get(i+1).getAccessibleText().equals("Begin")){
                    getCodeBlock(code.subList(i+2, code.size()));
                } else {
                    errorPopup("Please follow each If, While, and For block by a \"Begin\" block");
                }
                block.add("}");
            } else if (com.equals("END")){
                return;
            } else if (i == start.size() - 1){
                errorPopup("Please follow each \"BEGIN\" by an \"END\" block to contain your code");
            }
        }
    }

    /***
     *
     * @throws IOException      throws IOException to getFilePath()
     *
     * writes each ProgNode in the ArrayList, block, to the specified path
     */
    public void writeFile(){
        block = writeCode();
        if (block == null) {     //checks is the block has been returned as null, block is null when an error occurs
            return;             //skips the function to not write the file, file should not be written due to error
        }
         try(FileWriter file = new FileWriter("lightcode.ino");) {
             for (String node : block) {
                 file.write(node + "\n");
             }
             file.close();
         } catch (IOException e){
             System.out.println("Error, File name invalid");
        }
    }

    private boolean isBegin(int n) {
        if (n >= code.size()) {
            return false;
        } else {
            if ((code.get(n + 1).getAccessibleText().equals("Turn ")) ||
                    (code.get(n + 1).getAccessibleText().equals("If ")) ||
                    (code.get(n + 1).getAccessibleText().equals("While ")) ||
                    (code.get(n + 1).getAccessibleText().equals("Repeat ")) ||
                    (code.get(n + 1).getAccessibleText().equals("Forward")) ||
                    (code.get(n + 1).getAccessibleText().equals("Do_Nothing")) ||
                    (code.get(n + 1).getAccessibleText().equals("If_Crumb")) ||
                    (code.get(n + 1).getAccessibleText().equals("Drop_Crumb")) ||
                    (code.get(n + 1).getAccessibleText().equals("Eat_Crumb"))) {
                return code.get(n).getAccessibleText().equals("BEGIN");
            } else {
                errorPopup("There Must be Code between the BEGIN and END Blocks");
                return false;
            }
        }
    }

    /***
     *
     * @param n         the index of code being checked
     * @return if code at n is an END block
     */
    private boolean isEnd(int n) {
        if (n >= code.size()) {
            return false;
        } else {
            return code.get(n).getAccessibleText().equals("END");
        }
    }

    /***
     *
     * @param error     a String specifying the error
     *    makes a screen popup to tell the user an error has occurred
     */
    private void errorPopup(String error) {
        final Stage popup = new Stage();
        popup.initOwner(primaryStage);
        VBox content = new VBox();
        Label label = new Label("Error: " + error);
        content.getChildren().addAll(label);
        popup.setScene(new Scene(content));
        popup.show();
    }


    /***
     *
     * @return the groups of rectangles the StackPanes rest on and StackPanes representing the ProgNodes
     *
     */
    private Parent createCommands() {
        canvas.setStroke(Color.BLACK);
        canvas.setFill(Color.LIGHTGRAY);
        commandSpace.setStroke(Color.GRAY);
        commandSpace.setFill(Color.DARKGRAY);
         // list of all possible directions
        /*-----------------------------------------------------------------------------------*/
        /*------------------------------------COMMANDS---------------------------------------*/
        final Rectangle inner = new Rectangle(rect_width, rect_height);
        inner.setFill(Color.LIGHTCORAL);
        inner.setStroke(Color.LIGHTCORAL.darker());
        final StackPane forForever = new StackPane();
        forForever.getChildren().addAll(inner, new Label("Loop"));
        forForever.setTranslateX(rect_width + 40);
        forForever.setTranslateY(0);

        final StackPane setFlower = makeSetFlowerBlock("Set Flower #", Color.DODGERBLUE, rect_width, rect_height);
        setFlower.setTranslateX(startX);
        setFlower.setTranslateY(startY);
        setFlower.setAccessibleText("Set_Flower");
        startY += rect_height;

        final StackPane showFor = makeNumberBlock("Show for ", Color.POWDERBLUE, rect_width, rect_height, "ms");
        showFor.setTranslateX(startX);
        showFor.setTranslateY(startY);
        showFor.setAccessibleText("Show_for");
        startY += rect_height;

        final StackPane ifBlock = makeIfBlock("If ", Color.CORNFLOWERBLUE, rect_width, rect_height);
        ifBlock.setTranslateX(startX);
        ifBlock.setTranslateY(startY);
        ifBlock.setAccessibleText("If ");
        startY += rect_height;

        final StackPane elseBlock = makeBlock("Else ", Color.CORNFLOWERBLUE, rect_width, rect_height);
        elseBlock.setTranslateX(startX);
        elseBlock.setTranslateY(startY);
        elseBlock.setAccessibleText("Else ");
        startY += rect_height;

        final StackPane whileBlock = makeIfBlock("While ", Color.LIGHTBLUE, rect_width, rect_height);
        whileBlock.setTranslateX(startX);
        whileBlock.setTranslateY(startY);
        whileBlock.setAccessibleText("While ");
        startY += rect_height;


        final StackPane forBlock = makeNumberBlock("For ", Color.DEEPSKYBLUE, rect_width, rect_height, "times");
        forBlock.setTranslateX(startX);
        forBlock.setTranslateY(startY);
        forBlock.setAccessibleText("For ");
        startY += rect_height;

        final StackPane begin = makeBlock("BEGIN", Color.DARKCYAN, rect_width, rect_height);
        begin.setTranslateX(startX);
        begin.setTranslateY(startY);
        begin.setAccessibleText("BEGIN");
        startY += rect_height;


        final StackPane end = makeBlock("END", Color.DARKCYAN, rect_width, rect_height);
        end.setTranslateX(startX);
        end.setTranslateY(startY);
        end.setAccessibleText("END");
        startY += rect_height;

        group.getChildren().addAll(canvas, commandSpace, forForever, setFlower, showFor, ifBlock, elseBlock,
                whileBlock, forBlock, begin, end);
        return group;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @return a StackPane, holding the command String, with the color as a background, with the
     *                              specified width and height
     *                              onMouseClick the StackPane will make a draggable StackPane with the same parameters
     *                              and adds it to the group
     */
    private StackPane makeBlock(final String command, final Color color, final int
            width, final int height) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        rect.getChildren().addAll(inner, new Label(command));

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        rect.setOnMousePressed((MouseEvent me) -> {
            final StackPane com = makeRect(command, color, width, height);
            com.setTranslateX(x);
            com.setTranslateY(y);
            com.setAccessibleText(command);
            group.getChildren().add(com);
            com.onMouseDraggedProperty();
        });

        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @return a StackPane, holding the command String and a ComboBox, with the color as a background,
     *                              with the specified width and height
     *                              onMouseClick the StackPane will make a draggable StackPane with the same parameters
     *                              and adds it to the group
     */
    private StackPane makeSetFlowerBlock(final String command, final Color color, final int
            width, final int height) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();

        final TextField text = new TextField();
            text.setMaxWidth(50);
            text.setEditable(false);
        final ColorPicker colorPicker = new ColorPicker();
            colorPicker.setMaxWidth(100);
            colorPicker.setDisable(true);

        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);
        label.getChildren().addAll(new Label(command), text, colorPicker);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        //sets cursor over colorchooser to hand
        colorPicker.setCursor(Cursor.HAND);
        rect.setOnMousePressed((MouseEvent me) -> {
            // change of mouse's x and y values
            final StackPane com = makeSetFlowerRect(command, color, width, height);
            com.setTranslateX(x);
            com.setTranslateY(y);
            com.setAccessibleText(command);
            group.getChildren().add(com);
        });
        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param combo             the list of options for a ComboBox
     * @return a StackPane, holding the command String and a ComboBox, with the color as a background,
     *                              with the specified width and height
     *                              onMouseClick the StackPane will make a draggable StackPane with the same parameters
     *                              and adds it to the group
     */
    private StackPane makeNumberBlock(final String command, final Color color, final int
            width, final int height, String placeholder) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();

        final TextField text = new TextField();
        text.setMaxWidth(50);
        text.setPromptText(placeholder);
        text.setEditable(false);

        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);
        label.getChildren().addAll(new Label(command), text);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        rect.setOnMousePressed((MouseEvent me) -> {
            // change of mouse's x and y values
            final StackPane com = makeNumberRect(command, color, width, height, placeholder);
            com.setTranslateX(x);
            com.setTranslateY(y);
            com.setAccessibleText(command);
            group.getChildren().add(com);
        });
        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param combo             the list of options for a ComboBox
     * @return a StackPane, holding the command String and a ComboBox, with the color as a background,
     *                              with the specified width and height
     *                              onMouseClick the StackPane will make a draggable StackPane with the same parameters
     *                              and adds it to the group
     */
    private StackPane makeIfBlock(final String command, final Color color, final int
            width, final int height) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();

        final TextField text = new TextField();
        text.setMaxWidth(50);
        text.setEditable(false);

        ComboBox sensors = new ComboBox();
        sensors.getItems().addAll("Light", "Heat");
        sensors.setPromptText("Sensor");
        sensors.setDisable(true);

        ComboBox conditions = new ComboBox();
        conditions.getItems().addAll(">", "=", "<");
        conditions.setPromptText("Sign");
        conditions.setDisable(true);

        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);
        label.getChildren().addAll(new Label(command), sensors, conditions, text);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        rect.setOnMousePressed((MouseEvent me) -> {
            // change of mouse's x and y values
            final StackPane com = makeIfRect(command, color, width, height);
            com.setTranslateX(x);
            com.setTranslateY(y);
            com.setAccessibleText(command);
            group.getChildren().add(com);
        });
        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @return a draggable StackPane holding the command String, with the color as a
     *                              background, with the specified width and height, and adds it the code list
     */
    private StackPane makeRect(final String command, final Color color, final int
            width, final int height) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        rect.getChildren().addAll(inner, new Label(command));

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);

        rect.setOnMouseDragged((MouseEvent me) -> {
            // change of mouse's x and y values
            current = rect;
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            double newXPosition = initX + dragX; // delta of rectangle
            newXPosition -= newXPosition % tick; // coarse movement "snap2grid"
            double newYPosition = initY + dragY; // delta of rectangle
            newYPosition -= newYPosition % tick; // coarse movement "snap2grid"

            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= (rect_width + 40)) &&
                    (newXPosition <= (window_width) - width)) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        rect.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            current = rect;
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
        });

        rect.setOnMouseReleased((MouseEvent me) -> {
            current = null;
        });

        rect.setOnMouseDragOver((MouseEvent me) -> {
            rect.setOnMouseReleased((MouseEvent mouseEvent) -> {
                if (current != null){
                    current.setTranslateY(rect.getTranslateY() + rect_height);
                    current.setTranslateX(rect.getTranslateX() + 20);
                }
            });
        });
        code.add(rect);
        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param comboBox
     * @return a draggable StackPane holding the command String and a ComboBox, with the color as a
     *                              background, with the specified width and height, and adds it the code list
     */
    private StackPane makeSetFlowerRect(final String command, final Color color, final int
            width, final int height) {

        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);

        final TextField text = new TextField();
        text.setMaxWidth(50);
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setMaxWidth(100);

        label.getChildren().addAll(new Label(command), text, colorPicker);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        //sets cursor over colorchooser to hand
        colorPicker.setCursor(Cursor.HAND);
        rect.setOnMouseDragged((MouseEvent me) -> {
            current = rect;
            // change of mouse's x and y values
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            double newXPosition = initX + dragX; // delta of rectangle
            newXPosition -= newXPosition % tick; // coarse movement "snap2grid"
            double newYPosition = initY + dragY; // delta of rectangle
            newYPosition -= newYPosition % tick; // coarse movement "snap2grid"

            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= (rect_width + 40)) &&
                    (newXPosition <= (window_width - width))) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        rect.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            current = rect;
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
        });

        rect.setOnMouseReleased((MouseEvent me) -> {
            current = null;
        });

        rect.setOnMouseDragOver((MouseEvent me) -> {
            rect.setOnMouseReleased((MouseEvent mouseEvent) -> {
                if (current != null){
                    current.setTranslateY(rect.getTranslateY() + rect_height);
                    current.setTranslateX(rect.getTranslateX() + 20);
                }
            });
        });
        code.add(rect);
        return rect;
    }

    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param comboBox
     * @return a draggable StackPane holding the command String and a ComboBox, with the color as a
     *                              background, with the specified width and height, and adds it the code list
     */
    private StackPane makeNumberRect(final String command, final Color color, final int
            width, final int height, String placeholder) {

        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);

        final TextField text = new TextField();
        text.setMaxWidth(50);
        text.setPromptText(placeholder);

        label.getChildren().addAll(new Label(command), text);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        rect.setOnMouseDragged((MouseEvent me) -> {
            current = rect;
            // change of mouse's x and y values
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            double newXPosition = initX + dragX; // delta of rectangle
            newXPosition -= newXPosition % tick; // coarse movement "snap2grid"
            double newYPosition = initY + dragY; // delta of rectangle
            newYPosition -= newYPosition % tick; // coarse movement "snap2grid"

            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= (rect_width + 40)) &&
                    (newXPosition <= (window_width - width))) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        rect.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            current = rect;
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
        });

        rect.setOnMouseReleased((MouseEvent me) -> {
            current = null;
        });

        rect.setOnMouseDragOver((MouseEvent me) -> {
            rect.setOnMouseReleased((MouseEvent mouseEvent) -> {
                if (current != null){
                    current.setTranslateY(rect.getTranslateY() + rect_height);
                    current.setTranslateX(rect.getTranslateX() + 20);
                }
            });
        });
        code.add(rect);
        return rect;
    }


    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param comboBox
     * @return a draggable StackPane holding the command String and a ComboBox, with the color as a
     *                              background, with the specified width and height, and adds it the code list
     */
    private StackPane makeIfRect(final String command, final Color color, final int
            width, final int height) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);

        final TextField text = new TextField();
        text.setMaxWidth(50);
        ComboBox sensors = new ComboBox();
        sensors.getItems().addAll("Light", "Heat");
        sensors.setPromptText("sensor");
        ComboBox conditions = new ComboBox();
        conditions.getItems().addAll(">", "=", "<");
        conditions.setPromptText("sign");

        label.getChildren().addAll(new Label(command), sensors, conditions, text);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        //sets cursor over text to hand
        text.setCursor(Cursor.HAND);
        rect.setOnMouseDragged((MouseEvent me) -> {
            current = rect;
            // change of mouse's x and y values
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            double newXPosition = initX + dragX; // delta of rectangle
            newXPosition -= newXPosition % tick; // coarse movement "snap2grid"
            double newYPosition = initY + dragY; // delta of rectangle
            newYPosition -= newYPosition % tick; // coarse movement "snap2grid"

            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= (rect_width + 40)) &&
                    (newXPosition <= (window_width - width))) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        rect.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            current = rect;
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
        });

        rect.setOnMouseReleased((MouseEvent me) -> {
            current = null;
        });

        rect.setOnMouseDragOver((MouseEvent me) -> {
            rect.setOnMouseReleased((MouseEvent mouseEvent) -> {
                if (current != null){
                    current.setTranslateY(rect.getTranslateY() + rect_height);
                    current.setTranslateX(rect.getTranslateX() + 20);
                }
            });
        });
        code.add(rect);
        return rect;
    }


    /***
     *
     * @param command           a String representing the command
     * @param color             the color of the StackPane
     * @param width             the width of the StackPane
     * @param height            the height of the StackPane
     * @param comboBox
     * @return a draggable StackPane holding the command String and a ComboBox, with the color as a
     *                              background, with the specified width and height, and adds it the code list
     */
    private StackPane makeRect(final String command, final Color color, final int
            width, final int height, final ComboBox comboBox) {

        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();
        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);
        label.getChildren().addAll(new Label(command), comboBox);
        rect.getChildren().addAll(inner, label);


        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        rect.setOnMouseDragged((MouseEvent me) -> {
            current = rect;
            // change of mouse's x and y values
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            double newXPosition = initX + dragX; // delta of rectangle
            newXPosition -= newXPosition % tick; // coarse movement "snap2grid"
            double newYPosition = initY + dragY; // delta of rectangle
            newYPosition -= newYPosition % tick; // coarse movement "snap2grid"

            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= (rect_width + 40)) &&
                    (newXPosition <= (window_width - width))) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        rect.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            current = rect;
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
        });

        rect.setOnMouseReleased((MouseEvent me) -> {
            current = null;
        });

        rect.setOnMouseDragOver((MouseEvent me) -> {
            rect.setOnMouseReleased((MouseEvent mouseEvent) -> {
                if (current != null){
                    current.setTranslateY(rect.getTranslateY() + rect_height);
                    current.setTranslateX(rect.getTranslateX() + 20);
                }
            });
        });
        code.add(rect);
        return rect;
    }

}

/***
 *  compares StackPanes to sort them by their placement on the Y plane in ascending order
 */
class commandComparator implements Comparator<StackPane> {
    //sorts list of StackPanes by their position on the y axis in ascending order
    @Override
    public int compare(StackPane o1, StackPane o2) {
        return ((int) (o1.getTranslateY() - o2.getTranslateY()));
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
