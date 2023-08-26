import React, {useEffect, useState} from 'react';
import RssService, {RssItem, RssResponse} from "../service/RssService";

interface Props {
    title?: string
    renderItem(rssItem: RssItem): JSX.Element
    maxSize: number;
    feedName: string
}

export default function LiveFeed({title, feedName, maxSize, renderItem} : Props) {
    const [head, setHead] = useState<RssResponse>();
    const [items, setItems] = useState<RssItem[]>([]);

    useEffect(() => {
        RssService.getRssFeed(feedName).then(rssResult =>
            {
                setItems(rssResult.items);
                setHead(rssResult);
            }
        );

        RssService.getRssFeedSocket(feedName).onmessage = (message) => {
            addItem(message.data)
        }
    },[])

    const addItem = (newItem: RssItem) => {
        if (items.length >= maxSize) {
            setItems(prevItems => [newItem, ...prevItems.slice(0, maxSize - 1)]);
        } else {
            setItems(prevItems => [newItem, ...prevItems]);
        }
    };

    const getTitle = () : string => {
        if (title) {
            return title;
        }
        if (head) {
            return head.title;
        }
        return "Loading Feed..."
    }

    return (
        <div>
            <h5>{getTitle()}</h5>
            <ul className="p-0 mx-0 mt-0 mb-4 list-none">
                {items.map(item => (
                    <div>
                        <li key={item.id}></li>
                        <li className="flex align-items-center py-2 border-bottom-1 surface-border">
                            <span className="text-700">
                                {renderItem(item)}
                            </span>
                        </li>
                    </div>
                ))}
            </ul>
        </div>
    );
};
