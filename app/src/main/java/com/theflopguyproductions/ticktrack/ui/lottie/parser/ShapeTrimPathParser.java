package com.theflopguyproductions.ticktrack.ui.lottie.parser;


import com.theflopguyproductions.ticktrack.ui.lottie.LottieComposition;
import com.theflopguyproductions.ticktrack.ui.lottie.model.animatable.AnimatableFloatValue;
import com.theflopguyproductions.ticktrack.ui.lottie.model.content.ShapeTrimPath;
import com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader;

import java.io.IOException;

import static com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader.Options;

class ShapeTrimPathParser {

  private ShapeTrimPathParser() {}
  private static JsonReader.Options NAMES = Options.of(
      "s",
      "e",
      "o",
      "nm",
      "m",
      "hd"
  );
  static ShapeTrimPath parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    ShapeTrimPath.Type type = null;
    AnimatableFloatValue start = null;
    AnimatableFloatValue end = null;
    AnimatableFloatValue offset = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          start = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case 1:
          end = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case 2:
          offset = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case 3:
          name = reader.nextString();
          break;
        case 4:
          type = ShapeTrimPath.Type.forId(reader.nextInt());
          break;
        case 5:
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new ShapeTrimPath(name, type, start, end, offset, hidden);
  }
}
