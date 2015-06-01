# RoughSketch

## The friction logic

The easiest way to understand how the tPad works is that you send it a value (or series of values) from 0.0 - 1.0 that corresponds to the per cent that the device is ON. So:

```
tPad.sendFriction(1.0f);
```

Means turn the tPad all the way on. This works pretty well, and you could build a function that just sends a series of these commands, however, this means you'd have to handle all of your brush dynamics "by hand" which would suck. So, instead, the tPad group suggests that you sample from an image to get a series of values. Unfortunately, sampling takes some time, so you have to use a relatively simple prediction algorithm to send a series of floats, or a frictionBuffer:

```
float[] floatArray = {1.0, 2.0, 3.0};
tPad.sendFrictionBuffer(floatArray);
```

For convenience sake, I've tried to seperate the prediction algorithm from what we need to write at the moment. I've defined the following classes:

• TPadBrushHandler : manages brushes, events, TPad, and sending friction.
• Brush : an abstract class for all brushes.
• DefaultBrush : A concrete class for a single brush.

I've made it so that you only need to change one parameter and one function to get different brush behaviours (but I'm happy to change this if we need to). Each new Brush should have it's own *texture* and set of *weights*. 

Let's call any pixel on the texture *I[x][y]*. Let's call the weighting function *g(e)*, where *e* is the number of move events since the beginning of the gesture. Then friction at any point along the gesture will be *g(e)•I[x][y]*. Note that e >= 0, 0 <= g(e) <= 1. 

Each gesture needs a *length*. This should be the number of events it takes to complete the gesture.

```
start -> move -> move -> move -> move -> stop
```

The prediction algorithm works by predicting where your finger is going based on velocity and direction. It can therefore precompute along a trajectory, saving the values in the *predictedPixels* array, which has a prediction horizon (length) dynamically determined at runtime (but is really about 125).

At the moment, I've left g(e) as a static function, therefore it's possible to calculate g(e) in advance. The predicton algorithm therefore takes {g[e + 0]•I[x_0][y_0],g[e + 1]•I[x_1][y_1] ... g[e + n]•I[x_n][y_n]} as the predicted pixel array.

## What you need to do to define your own Brush

Copy from DefaultBrush and change:

```
		// Brush settings
		super.gestureLength = YOUR_N;
		setDataBitmap(R.drawable.YOUR_TEXTURE);
```

and

```
  private float g(int min, int max, int i) { YOUR_FUNCTION };
```

To add an image to R.drawable, just drop any {jpg,png,bmp...} into res/drawable and do a clean rebuild. Be careful, though, don't rebuild everything in the workspace! It deleted all of my jar files when I did that.
