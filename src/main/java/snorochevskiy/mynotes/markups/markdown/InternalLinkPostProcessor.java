package snorochevskiy.mynotes.markups.markdown;

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.DoNotLinkDecorate;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.InlineLinkNode;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.LinkNode;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.CharSubSequence;

public class InternalLinkPostProcessor extends NodePostProcessor {

    private String noteRelativePath;

    public InternalLinkPostProcessor(Document document) {

    }

    @Override
    public void process(NodeTracker nodeTracker, Node node) {

        if (node instanceof Image) {
            Image image = (Image)node;

            if (!image.getUrl().toString().contains("/") ) {
                BasedSequence relativePathSequence = CharSubSequence.of("file://")
                        .append(noteRelativePath)
                        .append("/");
                image.setUrl(relativePathSequence.append(image.getUrl()));
            }
        }

        if (node instanceof Link) {
            Link link = (Link)node;
        }

    }

    public static class Factory extends NodePostProcessorFactory {

        private final String noteRelativePath;

        public Factory(String noteRelativePath) {
            super(false);
            this.noteRelativePath = noteRelativePath;
            addNodeWithExclusions(Image.class);
            addNodeWithExclusions(Link.class);
        }

        @Override
        public NodePostProcessor create(Document document) {
            InternalLinkPostProcessor processor = new InternalLinkPostProcessor(document);
            processor.noteRelativePath = noteRelativePath;
            return processor;
        }
    }
}
