import { useState } from "react";
import { Link } from "react-router-dom";
import DashboardIcon from "@mui/icons-material/Dashboard";
import DevicesIcon from "@mui/icons-material/Devices";
import AddBoxIcon from "@mui/icons-material/AddBox";
import ApiIcon from "@mui/icons-material/Api";
import FetchSensors from "./FetchSensors";

const Sidebar = () => {
  const [open, setOpen] = useState(true);
  const [sensors, setSensors] = useState([]);

  const toggleSidebar = () => {
    setOpen(!open);
  };

  return (
    <div className="flex">
      <div
        className={`${
          open ? "w-28" : "w-16" // Smaller width for open state
        } bg-gray-800 h-screen p-2 pt-2 relative duration-300 shadow-md fixed top-0 left-0`}
      >
        <ul className="pt-2">
          <li className="flex items-center text-gray-300 cursor-pointer p-1 hover:bg-gray-700 text-sm">
            <DashboardIcon fontSize="small" /> {/* Smaller icon */}
            <Link to="/" className={`${!open && "hidden"} ml-2 text-sm`}>Dashboard</Link> {/* Smaller text */}
          </li>
          <FetchSensors setSensors={setSensors} />
          {sensors.length > 0 ? (
            <li className="flex flex-col">
              <div className="flex items-center text-gray-300 cursor-pointer p-1 hover:bg-gray-700 text-sm">
                <DevicesIcon fontSize="small" />
                <span className={`${!open && "hidden"} ml-2 text-sm`}>Sensors</span>
              </div>
              <ul className={`ml-2 ${!open ? "hidden" : ""}`}>
                {sensors.map((sensor, index) => (
                  <li key={index} className="text-gray-300 cursor-pointer p-1 hover:bg-gray-700 text-sm">
                    <Link to={`/sensor/${sensor}`}>{sensor}</Link>
                  </li>
                ))}
              </ul>
            </li>
          ) : (
            <li className="text-gray-300 p-1 text-sm">No Sensors Available</li>
          )}
          <li className="flex items-center text-gray-300 cursor-pointer p-1 hover:bg-gray-700 text-sm">
            <ApiIcon fontSize="small" />
            <Link to="/api-usage" className={`${!open && "hidden"} ml-2 text-sm`}>API Usage</Link>
          </li>
        </ul>
      </div>
      <div className={`flex-1 ml-${open ? '28' : '16'} p-4`} style={{ marginTop: '60px' }}>
        {/* Main content goes here */}
      </div>
    </div>
  );
};

export default Sidebar;
