import React, {useState} from 'react';

interface MenuProvider {
    activeMenu: string,
    setActiveMenu(activeMenu: string)
}

export const MenuContext = React.createContext({} as MenuProvider);

export const MenuProvider = (props) => {
    const [activeMenu, setActiveMenu] = useState('');

    const value = {
        activeMenu,
        setActiveMenu
    } as MenuProvider;

    return <MenuContext.Provider value={value}>{props.children}</MenuContext.Provider>;
};
