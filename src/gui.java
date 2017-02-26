/**
 * Created by Ariel on 2/25/2017.
 */
import javafx.application.Application;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class gui extends Application {
    private final static int window_height = 800;
    private final static int window_width = 1000;
    private final static int rect_height = 40;
    private final static int rect_width = 220;
    private final static int startX = 20;
    private int startY = 20;
    private final static int x = window_width - rect_width;
    private final static int y = 0;
    private final static int tick = 20;
    private Group group;
    private ArrayList<StackPane> code;
    private Stage primaryStage;
    private final Rectangle canvas = new Rectangle(window_width, window_height);
    private final Rectangle commandSpace = new Rectangle(rect_width + 40, window_height);
    private double initY;
    private double initX;
    private Point2D dragAnchor;
    private int i;
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
            writeCode();
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
        for (i = 0; i < code.size(); i++) {
            StackPane s = code.get(i);
            String com = s.getAccessibleText();
            if (com.equals("Set_Flower")) {

            } else if (com.equals("Drop")) {

            } else if (com.equals("Eat_Crumb")) {

            } else if (com.equals("Drop_Crumb")) {

            } else if (com.equals("Turn ")){
                ComboBox c = (ComboBox) ((HBox) s.getChildren().get(1)).getChildren().get(1);

            } else if (com.equals("While ")) {

            } else if (com.equals("If ")) {

            } else if (com.equals("If_Crumb")) {

            } else if (com.equals("Repeat ")) {

            } else if (com.equals("Do Nothing")) {

            } else if (com.equals("Halt")) {

            }
        }
        return block;
    }

    /***
     * \
     * @param filepath          takes in a String for the filepath to write the file
     * @throws IOException      throws IOException to getFilePath()
     *
     * writes each ProgNode in the ArrayList, block, to the specified path
     */
    public void writeFile() throws IOException {
        ArrayList<String> block = writeCode();
        if (block == null) {     //checks is the block has been returned as null, block is null when an error occurs
            return;             //skips the function to not write the file, file should not be written due to error
        }
        FileWriter file = new FileWriter("lightcode.ino");
        for (String node : block) {
            file.write(node);
        }
        file.close();
    }

    /***
     *
     * @return recursively returns an ArrayList of a portion the ProgNodes in the file
     */
    private ArrayList<String> getCodeBlock(List<StackPane> start) {
        ArrayList<String> block = new ArrayList<>();
        for (int i = 0; i < start.size(); i++) {
            StackPane s = start.get(i);
            String com = s.getAccessibleText();
            if (com.equals("Forward")) {

            } else if (com.equals("Drop")) {

            } else if (com.equals("Eat_Crumb")) {

            } else if (com.equals("Drop_Crumb")) {

            } else if (com.equals("Turn ")) {
                ComboBox c = (ComboBox) ((HBox) s.getChildren().get(1)).getChildren().get(1);

            } else if (com.equals("While ")) {

            } else if (com.equals("If ")) {

            } else if (com.equals("If_Crumb")) {

            } else if (com.equals("Repeat ")) {

            } else if (com.equals("Do Nothing")) {

            } else if (com.equals("END")) {
                return block;
            }
        }
        return block;
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
     * opens a fileChooser for the user to pick the file and filepath to save their code to
     *      calls writeFile with the chosen FilePath
     *      handles the IOException
     */
    public void getFilepath() {
        final Stage popup = new Stage();
        popup.initOwner(primaryStage);
        final JFileChooser chooser = new JFileChooser();
        int n = JFileChooser.OPEN_DIALOG;
        int m = JFileChooser.SAVE_DIALOG;
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                writeFile(chooser.getSelectedFile().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        /*--------------------------------------------------------------------------------------------------*/
        /*-------------------ComboBoxes and their array lists and observable lists--------------------------*/
        // list of all possible directions
        ArrayList<String> dir = new ArrayList<>();
        dir.add("AHEAD");
        dir.add("RIGHT");
        dir.add("LEFT");
        dir.add("BEHIND");

        //list of all possible blocked and open access in all directions
        //      included crumb and no crumb
        ArrayList<String> access = new ArrayList<>();
        for (String d : dir) {
            access.add("OPEN " + d);
        }
        for (String d : dir) {
            access.add("BLOCKED " + d);
        }

        // ComboBox for all possible directions
        ObservableList<String> dirOpt = FXCollections.observableArrayList();
        dirOpt.addAll(dir);

        // all possible if statements, access available and blocked in all directions, crumb, no crumb
        ObservableList<String> ifStatements = FXCollections.observableArrayList();
        ifStatements.addAll(access);

        ObservableList<String> numbers = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7",
                "8", "9");

        /*-----------------------------------------------------------------------------------*/
        /*------------------------------------COMMANDS---------------------------------------*/
        final Rectangle inner = new Rectangle(rect_width, rect_height);
        inner.setFill(Color.LIGHTCORAL);
        inner.setStroke(Color.LIGHTCORAL.darker());
        final StackPane forForever = new StackPane();
        forForever.getChildren().addAll(inner, new Label("Loop"));
        forForever.setTranslateX(rect_width + 40);
        forForever.setTranslateY(0);

        final StackPane setFlower = makeBlock("Set Flower", Color.DODGERBLUE, rect_width, rect_height);
        setFlower.setTranslateX(startX);
        setFlower.setTranslateY(startY);
        setFlower.setAccessibleText("Set_Flower");
        startY += rect_height;

        final StackPane showFor = makeBlock("Show for ", Color.POWDERBLUE, rect_width, rect_height);
        showFor.setTranslateX(startX);
        showFor.setTranslateY(startY);
        showFor.setAccessibleText("Show_for");
        startY += rect_height;

        final StackPane lightSensor = makeBlock("Light Sensor", Color.DEEPSKYBLUE, rect_width, rect_height);
        lightSensor.setTranslateX(startX);
        lightSensor.setTranslateY(startY);
        lightSensor.setAccessibleText("Do_Nothing");
        startY += rect_height;

        final StackPane ifBlock = makeBlock("If ", Color.CORNFLOWERBLUE, rect_width, rect_height, ifStatements);
        ifBlock.setTranslateX(startX);
        ifBlock.setTranslateY(startY);
        ifBlock.setAccessibleText("If ");
        startY += rect_height;

        final StackPane whileBlock = makeBlock("While ", Color.LIGHTBLUE, rect_width, rect_height, ifStatements);
        whileBlock.setTranslateX(startX);
        whileBlock.setTranslateY(startY);
        whileBlock.setAccessibleText("While ");
        startY += rect_height;


        final StackPane forBlock = makeBlock("For ", Color.DEEPSKYBLUE, rect_width, rect_height, numbers);
        forBlock.setTranslateX(startX);
        forBlock.setTranslateY(startY);
        forBlock.setAccessibleText("For ");
        startY += rect_height;


        group.getChildren().addAll(canvas, commandSpace, forForever, setFlower, showFor, lightSensor, ifBlock,
                whileBlock, forBlock);
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
     * @param combo             the list of options for a ComboBox
     * @return a StackPane, holding the command String and a ComboBox, with the color as a background,
     *                              with the specified width and height
     *                              onMouseClick the StackPane will make a draggable StackPane with the same parameters
     *                              and adds it to the group
     */
    private StackPane makeBlock(final String command, final Color color, final int
            width, final int height, final ObservableList combo) {
        final Rectangle inner = new Rectangle(width, height);
        inner.setFill(color);
        inner.setStroke(color.darker());
        final StackPane rect = new StackPane();

        final ComboBox comboBox = new ComboBox(combo);
        comboBox.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY,
                Insets.EMPTY)));
        comboBox.setPromptText("pick a statement");

        HBox label = new HBox();
        label.setAlignment(Pos.CENTER);
        label.getChildren().addAll(new Label(command), comboBox);
        rect.getChildren().addAll(inner, label);

        // sets cursor over rectangle to hand
        rect.setCursor(Cursor.HAND);
        rect.setOnMousePressed((MouseEvent me) -> {
            // change of mouse's x and y values
            final ComboBox newCombo = new ComboBox(combo);
            newCombo.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY,
                    Insets.EMPTY)));
            newCombo.setPromptText("pick a statement");
            final StackPane com = makeRect(command, color, width, height, newCombo);
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
        //sets cursor over comboBox to hand
        comboBox.setCursor(Cursor.HAND);
        comboBox.setOnMouseDragged((MouseEvent me) -> {
            // change of mouse's x and y values
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();

            //new x and y values of the rectangle
            double newXPosition = initX + dragX;
            newXPosition -= newXPosition % tick;
            double newYPosition = initY + dragY;
            newYPosition -= newYPosition % tick;
            // check that the rectangle is not moving outside the bounds of the window before
            //      changing the rectangles position
            if ((newXPosition >= 0) &&
                    (newXPosition <= window_width - width)) {
                rect.setTranslateX(newXPosition);
            }
            if ((newYPosition >= 0) &&
                    (newYPosition <= window_height - height)) {
                rect.setTranslateY(newYPosition);
            }
        });

        comboBox.setOnMousePressed((MouseEvent me) -> {
            //stores initial x and y value of the x for the next time the rectangle is dragged
            initX = rect.getTranslateX();
            initY = rect.getTranslateY();
            dragAnchor = new Point2D(
                    me.getSceneX(), me.getSceneY()
            );
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
