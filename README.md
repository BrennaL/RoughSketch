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

I've made it so that you only need to change one parameter and one function to get different brush behaviours (but I'm happy to change this if we need to). Each new Brush should have it's own *texture* and set of *weights*. The texture is like the 
--- MORE SOON!


