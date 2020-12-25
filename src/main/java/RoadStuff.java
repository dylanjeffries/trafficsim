//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//
//public class RoadStuff {
//
//    //Camera
//    private OrthographicCamera camera;
//
//    //Mouse Button States
//    private boolean leftPressed;
//    private boolean middlePressed;
//    private boolean rightPressed;
//
//    //Cursor Locations
//    private Vector3 cursorPos;
//    private Vector2 cursorIndex;
//
//    //Building
//    private Road newRoad;
//    private boolean buildValidity;
//    private boolean proxValidity;
//    private boolean inlineValidity;
//
//    public RoadStuff() {
//        newRoad = null;
//        buildValidity = false;
//        proxValidity = false;
//        inlineValidity = false;
//    }
//
//    public void update() {
//        buildValidity = proxValidity && inlineValidity;
//    }
//
//    public void mouseMoved(int screenX, int screenY) {
//
//        //Cursor Location Updates
//        cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
//        cursorIndex = new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize));
//
//        if (newRoad == null) {
//            //Prox for current cursor cell
//        } else {
//            //Prox for all cells within new road
//            inlineValidity = newRoad.isIndexInline(cursorIndex);
//            if (inlineValidity) {
//                newRoad.setEndCell(grid.get(cursorIndex));
//            } else {
//                newRoad.setEndCell(newRoad.getStartCell());
//            }
//            newRoad.recalculate();
//        }
//    }
//
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        if (button == 0) {
//            leftPressed = true;
//            Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
//            Vector2 cursorIndex = new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize));
//            if (buildingMode == BuildingMode.ROAD) {
//                if (roadInProgress == null) {
//                    roadInProgress = new Road("R" + roadCounter,
//                            grid.get(cursorIndex),
//                            grid.get(cursorIndex),
//                            textures.get("road_tp"));
//                } else {
//                    if (buildValidity) {
//                        roadInProgress.setTexture(textures.get("road"));
//                        for (Vector2 v : roadInProgress.getCellIndices()) {
//                            grid.get(v).setRoad(roadInProgress);
//                        }
//                        roads.put("R" + roadCounter, roadInProgress);
//                        roadCounter++;
//                        roadInProgress = null;
//                    }
//                }
//            }
//        }
//        else if (button == 1) { rightPressed = true; }
//        else if (button == 2) { middlePressed = true; }
//        return super.touchDown(screenX, screenY, pointer, button);
//    }
//
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        if (leftPressed) {
//            Vector3 cursorPos = camera.unproject(new Vector3(screenX, screenY, 0));
//            //grid.get(new Vector2((int)(cursorPos.x/gridCellSize), (int)(cursorPos.y/gridCellSize))).setRoad(roadPaint);
//        } else if (middlePressed) {
//            camera.translate(-(float)Gdx.input.getDeltaX(), (float)Gdx.input.getDeltaY());
//            camera.update();
//        }
//        return true;
//    }
//
//    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        if (button == 0) { leftPressed = false; }
//        else if (button == 1) { rightPressed = false; }
//        else if (button == 2) { middlePressed = false; }
//        return super.touchUp(screenX, screenY, pointer, button);
//    }
//
//}
