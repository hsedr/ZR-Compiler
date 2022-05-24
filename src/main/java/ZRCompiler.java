public class ZRCompiler {

    ZRTokenizer lexer = ZRTokenizer.getInstance();
    Token lookAhead;

    /**
     * Starting point of the parsing process.
     * @return returns an HTML document as a String.
     */
    public String Start() {
        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>ZeichenRoboter</title>
                  </head>
                  <body>
                    <canvas
                      id="canvas"
                      width="500"
                      height="500"
                      style="border: 1px solid #000000"
                    >
                    </canvas>
                    <script>
                      const canvas = document.getElementById("canvas");
                      const ctx = canvas.getContext("2d");
                      ctx.fillSytle = "black";
                      ctx.strokeStyle = "white";
                      ctx.fillRect(0, 0, canvas.width, canvas.height);
                                
                      class Vector2 {
                        constructor(x, y) {
                          this.x = x;
                          this.y = y;
                        }
                                
                        /**
                         * @param {Vector2} v1
                         * @param {Vector2} v2
                         */
                        static add(v1, v2) {
                          return new Vector2(v1.x + v2.x, v1.y + v2.y);
                        }
                                
                        /**
                         * @param {Vector2} v
                         * @param {number} alpha
                         */
                        static rotate(v, alpha) {
                          alpha = alpha * (Math.PI / 180);
                          var x = Math.cos(alpha) * v.x - Math.sin(alpha) * v.y;
                          var y = Math.sin(alpha) * v.x + Math.cos(alpha) * v.y;
                          return new Vector2(
                            Math.round(x * 10000) / 10000,
                            Math.round(y * 10000) / 10000
                          );
                        }
                                
                        /**
                         * @param {Vector2} v1
                         * @param {Vector2} v2
                         */
                        static mul(v1, v2) {
                          var x = v1.x * v2.x;
                          var y = v1.y * v2.y;
                          return new Vector2(x, y);
                        }
                                
                        /**
                         * @param {Vector2} v1
                         * @param {number} num
                         */
                        static mul(v1, num) {
                          var x = v1.x * num;
                          var y = v1.y * num;
                          return new Vector2(x, y);
                        }
                                
                        /**
                         * @param {number} num
                         */
                        mul(num) {
                          var x = this.x * num;
                          var y = this.y * num;
                          return new Vector2(x, y);
                        }
                                
                        /**
                         * @param {Vector2} v
                         */
                        static normalized(v) {
                          return new Vector2(v.x, v.y).mul(1 / v.magnitude());
                        }
                                
                        normalized() {
                          return new Vector2(this.x, this.y).mul(1 / this.magnitude());
                        }
                                
                        magnitude() {
                          var sqrX = Math.pow(this.x, 2);
                          var sqrY = Math.pow(this.y, 2);
                          return Math.sqrt(sqrX + sqrY);
                        }
                      }
                      class Turtle {
                        /**
                         * @param {Vector2} posV
                         * @param {Vector2} dirV
                         */
                        constructor(posV, dirV) {
                          this.posV = posV;
                          this.dirV = dirV;
                          this.drawing = false;
                        }
                                
                        /**
                         * @param {number} angle
                         */
                        turnRight(angle) {
                          this.dirV = Vector2.rotate(this.dirV, angle);
                        }
                                
                        move(num) {
                          ctx.moveTo(this.posV.x, this.posV.y);
                          this.posV = Vector2.add(this.posV, this.dirV.normalized().mul(num));
                          ctx.lineTo(this.posV.x, this.posV.y);
                          ctx.stroke();
                        }
                                
                         setPen() {
                            if (this.drawing) this.drawing = false;
                            else {
                                this.drawing = true;
                                ctx.beginPath(this.posV.x, this.posV.y);      
                            }
                         }
                         
                         setColor(color){
                            ctx.strokeStyle = color;
                         }
                         
                         penWidth(num){
                            ctx.lineWidth = parseInt(num);
                         }
                      }
                      var turtle = new Turtle(new Vector2(250, 450), new Vector2(0, -1));
                      %s
                      console.log(turtle);
                    </script>
                  </body>
                </html>
                """, Commands());
    }

    /**
     * Compiles entered commands.
     * Returns empty String if lookahead is not in Follow Set of
     * the Commands rule.
     *
     * @return compiled commands
     */
    private String Commands() {
        lookAhead = lexer.nextToken();
        //matching to elements of FIRST set
        if (lookAhead.tokenCLass().matches("WH|VW|RE|STIFT|FARBE")) {
            return Command() + "\n\t" + Commands();
        }
        return "";
    }

    /**
     * Applies production rule to current lookahead.
     *
     * @return compiled Command
     */
    private String Command() {
        switch (lookAhead.tokenCLass()) {
            case "WH":
                return WH();
            case "VW":
                return VW();
            case "RE":
                return RE();
            case "STIFT":
                return STIFT();
            case "FARBE":
                return FARBE();
        }
        return "";
    }

    /**
     * Production rule for WH.
     *
     * @return compiled WH command
     */
    private String WH() {
        //repetitions
        int number;
        String command = "";
        lookAhead = lexer.nextToken();
        if (lookAhead.tokenCLass().equals("Number")) {
            number = Integer.parseInt(lookAhead.value());
            lookAhead = lexer.nextToken();
            if (lookAhead.tokenCLass().equals("[")) {
                String _command = Commands();
                if (lookAhead.tokenCLass().equals("]")) {
                    for (int i = 0; i < number; i++) {
                        command += _command;
                    }
                    return command;
                } else error("]");
            } else error("[");
        } else error("Number");
        return "";
    }

    /**
     * Production rule for VW
     *
     * @return compiled VW command
     */
    private String VW() {
        lookAhead = lexer.nextToken();
        if (lookAhead.tokenCLass().equals("Number")) return String.format("turtle.move(%s);", lookAhead.value());
        else error("Number");
        return "";
    }

    /**
     * Production rule for RE.
     *
     * @return compiled RE command
     */
    private String RE() {
        lookAhead = lexer.nextToken();
        if (lookAhead.tokenCLass().equals("Number")) return String.format("turtle.turnRight(%s);", lookAhead.value());
        else error("Number");
        return "";
    }

    /**
     * Production rule for STIFT.
     *
     * @return compiled STIFT command
     */
    private String STIFT() {
        lookAhead = lexer.nextToken();
        if (lookAhead.tokenCLass().equals("Number")) return String.format("turtle.penWidth(%s);\n turtle.setPen();", lookAhead.value());
        else error("Number");
        return "";
    }

    /**
     * Production rule for FARBE.
     *
     * @return compiled FARBE command
     */
    private String FARBE() {
        lookAhead = lexer.nextToken();
        if (lookAhead.tokenCLass().equals("Farbwert")) return String.format("turtle.setColor('%s');", lookAhead.value());
        else error("red|black|white|yellow|green|blue|orange|purple");
        return "";
    }

    /**
     * Handles unexpected Tokens.
     *
     * @param expectedToken Token expected at current position
     */
    private void error(String expectedToken) {
        int position = lookAhead.position();
        System.out.println("Expected '" + expectedToken + "' at position " + position + ", given " + lookAhead.value());
        System.exit(-1);
    }
}