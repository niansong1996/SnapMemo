using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Graphics.Imaging;
using Windows.Storage.Streams;
using Windows.UI.Xaml.Media.Imaging;

namespace SnapMemo.src.tool
{
    public class PictureConvert
    {
        public static async Task<SoftwareBitmapSource> FromStream(IRandomAccessStream stream)
        {
            // Create the decoder from the stream
            var decoder = await BitmapDecoder.CreateAsync(stream);

            // Get the SoftwareBitmap representation of the file
            SoftwareBitmap picture = await decoder.GetSoftwareBitmapAsync();

            SoftwareBitmap bitmapBGRA8 = SoftwareBitmap.Convert(picture, BitmapPixelFormat.Bgra8, BitmapAlphaMode.Premultiplied);

            var source = new SoftwareBitmapSource();
            await source.SetBitmapAsync(bitmapBGRA8);

            return source;
        }
    }
}
