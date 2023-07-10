import {InputText} from 'primereact/inputtext';
import {Tag} from 'primereact/tag';
import React, {useState} from "react";

interface InputTextTag {
    onChange(newTags: string[]): void,
    lable: string
}

export default function InputTextTags({onChange, lable} : InputTextTag){

    const [tagInput, setTagInput] = useState<string>("");
    const [tags, setTags] = useState<string[]>([]);

    function afterSubmission (e: React.SyntheticEvent ){
       e.preventDefault();
       if(tagInput.length === 0) {
            return;
       }
       const newValue = [...tags, tagInput];
       setTags(newValue);
       setTagInput("");
       onChange(newValue);
    }

    function removeTag (tagToRemove : string) {
        const newValue = tags.filter(tag => tag != tagToRemove)

        setTags(newValue);
        onChange(newValue);
    }



    return <div>
                <form onSubmit = {afterSubmission}>
                    <label> {lable}:
                        <InputText
                            type = "text"
                            name = "itemName"
                            value = {tagInput}
                            onChange = {(e) => {
                                setTagInput(e.target.value);
                            }}
                        />
                    </label>
                </form>
                <div>
                {tags.map(tag =>
                    <Tag value={tag}>
                        <div className="flex align-items-center gap-2">
                            <i className="pi pi-times text-xs" onClick={() => removeTag(tag)}></i>
                        </div>
                    </Tag>
                    )}
                </div>
            </div>
}
