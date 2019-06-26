package mosaic;

import org.springframework.stereotype.Service;

@Service
public class MosaicService implements ImageTransformer {
    @Override
    public byte[] transform(byte[] image) {
        byte[] out = new byte[image.length];
        return out;
    }
}
