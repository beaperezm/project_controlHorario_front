package com.proyectodam.fichappclient.util;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Clase auxiliar para habilitar el redimensionado de un Stage sin bordes.
 */
public class WindowResizeHelper {

    private static final int RESIZE_MARGIN = 5;
    private static final Set<Scene> registeredScenes =
            Collections.newSetFromMap(new WeakHashMap<>());

    public static void addResizeListener(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null || registeredScenes.contains(scene)) return;
        registeredScenes.add(scene);

        ResizeListener resizeListener = new ResizeListener(stage);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        scene.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        scene.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
    }

    private static class ResizeListener implements EventHandler<MouseEvent> {
        private final Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private double startX = 0;
        private double startY = 0;

        public ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                if (stage.isMaximized()) {
                    setCursor(scene, Cursor.DEFAULT);
                    return;
                }

                if (mouseEventX < RESIZE_MARGIN && mouseEventY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < RESIZE_MARGIN && mouseEventY > sceneHeight - RESIZE_MARGIN) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - RESIZE_MARGIN && mouseEventY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - RESIZE_MARGIN && mouseEventY > sceneHeight - RESIZE_MARGIN) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < RESIZE_MARGIN) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - RESIZE_MARGIN) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < RESIZE_MARGIN) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - RESIZE_MARGIN) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                setCursor(scene, cursorEvent);

            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                if (Cursor.DEFAULT.equals(cursorEvent)) {
                    return; // El arrastre normal se gestiona en otro lugar
                }
                
                if (stage.isMaximized()) {
                    return;
                }

                if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                    double minHeight = stage.getMinHeight() > (RESIZE_MARGIN * 2) ? stage.getMinHeight() : (RESIZE_MARGIN * 2);
                    if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                        if (stage.getHeight() > minHeight || mouseEventY < 0) {
                            double height = stage.getHeight() - mouseEvent.getScreenY() + stage.getY();
                            if (height >= minHeight) {
                                stage.setHeight(height);
                                stage.setY(mouseEvent.getScreenY());
                            }
                        }
                    } else {
                        if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                            stage.setHeight(mouseEventY + startY);
                        }
                    }
                }

                if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                    double minWidth = stage.getMinWidth() > (RESIZE_MARGIN * 2) ? stage.getMinWidth() : (RESIZE_MARGIN * 2);
                    if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                        if (stage.getWidth() > minWidth || mouseEventX < 0) {
                            double width = stage.getWidth() - mouseEvent.getScreenX() + stage.getX();
                            if (width >= minWidth) {
                                stage.setWidth(width);
                                stage.setX(mouseEvent.getScreenX());
                            }
                        }
                    } else {
                        if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                            stage.setWidth(mouseEventX + startX);
                        }
                    }
                }
            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                setCursor(scene, Cursor.DEFAULT);
            }
        }

        private void setCursor(Scene scene, Cursor cursor) {
            scene.setCursor(cursor);
        }
    }
}
