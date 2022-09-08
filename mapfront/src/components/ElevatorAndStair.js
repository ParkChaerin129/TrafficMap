import React from 'react';
import { useState, useEffect, useRef } from 'react';
import { ListGroup } from 'react-bootstrap';

const SideBar =({width=200, children})=>{
    const [isOpen, setOpen] = useState(false);
    const [xPosition, setX] = useState(-width);
    const [boxShadow, setBoxShadow] = useState();
    const [chevron, setChevron] = useState("bi bi-chevron-double-left");
    const side = useRef();

    const toggleMenu = () => {
        if(xPosition < 0){
            setChevron("bi bi-chevron-double-right");
            setX(0);
            setBoxShadow('1px 3px 15px 1px gray');
            setOpen(true);
        }else{
            setChevron("bi bi-chevron-double-left");
            setX(-width);
            setBoxShadow('none');
            setOpen(false);
        }
    }
    const handleClose = async e => {
        console.log("클릭");
        console.log(isOpen);
        let sideArea = side.current;
        let sideCildren = side.current.contains(e.target);
        if (isOpen && (!sideArea || !sideCildren)) {
            await setX(-width); 
            await setOpen(false);
        }
    }
    const handleCheckBox = (id) => {
        console.log(id);
        console.log("머임");
    }

    useEffect(()=>{
        console.log(children);
        window.addEventListener('click', handleClose);
        return () => {
            window.removeEventListener('click', handleClose);
        };
    })

    useEffect(()=>{
        console.log(children);
    }, [children])


    return(
        <div className="sidebarmain">
            <div ref={side}  className="innersidbare" style={{ width: `${width}px`, height: "110px", top:"60%", right: "0px", transform: `translatex(${-xPosition}px)`, boxShadow: boxShadow}}>
                <button  onClick={() => toggleMenu()}
                className="sidebarbuttone" style={{left: "-120px"}} >
                <i class={chevron} style={{border: ""}}></i>
                </button>
                <div className="" style={{position: "relative", top: "-95px", height: "100%"}}>
                    <div className='list-group' style={{}}>
                        {children.map((obj)=>{
                            console.log(obj);
                            return(
                                <div className="row">
                                    <div className="">
                                        <label for="chk">
                                            <input type="checkbox" id="chk"  style={{width: "35px", float: "left", marginTop: "10px", marginLeft: "10px"}}></input>
                                            <i class="circle"></i>
                                            {obj.data}
                                        </label>
                                    </div>
                                    {/* <div className="col-9" style={{alignSelf: "center", marginTop: "10px"}}>
                                        <img src={obj.src} style={{width: "20px", height: "20px"}}></img>
                                        {obj.data}
                                    </div> */}
                                </div>
                            );
                        })}
                    </div>  
                </div> 
            </div>
      </div>
    );
}

export default SideBar; 
